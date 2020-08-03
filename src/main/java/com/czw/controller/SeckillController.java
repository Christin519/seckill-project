package com.czw.controller;

import com.czw.access.AccessLimit;
import com.czw.bean.GoodsExtend;
import com.czw.bean.OrderInfo;
import com.czw.bean.OrderSeckill;
import com.czw.bean.User;
import com.czw.config.SeckillConfig;
import com.czw.dto.ResultDTO;
import com.czw.enums.CodeMsg;
import com.czw.rabbitmq.MQSender;
import com.czw.rabbitmq.SeckillMessage;
import com.czw.redis.GoodsKey;
import com.czw.service.GoodsService;
import com.czw.service.OrderService;
import com.czw.service.RedisService;
import com.czw.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author: ChengZiwang
 * @date: 2020/7/23
 **/
@Controller
public class SeckillController {

    //订单相关业务
    @Autowired
    private OrderService orderService;

    //商品相关业务
    @Resource
    private GoodsService goodsService;

    //秒杀业务
    @Resource
    private SeckillService seckillService;

    @Resource
    RedisService redisService;

    @Autowired
    MQSender sender;

    public static Map<Long, Boolean> localOverMap =  SeckillConfig.localOverMap;

    @ResponseBody
    @RequestMapping("/reset")
    public String reset(){
        SeckillConfig.localOverMap=null;
        List<GoodsExtend> list = goodsService.selectGE();
        list.stream().forEach(i -> test(i.getId()));
        return "success";
    }

    void test(Long id){
        GoodsExtend goods = goodsService.selectGEById(id);
        //写缓存
        redisService.set(GoodsKey.getGoodsId,id.toString(),goods);
        redisService.set(GoodsKey.getSeckillGoodsStock,""+id,goods.getStockCount());
        localOverMap.put(id,false);
    }


    /*
    QPS:
        500*10    820，1197，1220
        5000*10     572
     */

    @RequestMapping(value="/{path}/seckillMQ", method= RequestMethod.POST)
    @ResponseBody
    public ResultDTO<Integer> miaosha(Model model, User user,@PathVariable("path") String path,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return ResultDTO.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = seckillService.checkPath(user, goodsId, path);
        if(!check){
            return ResultDTO.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return ResultDTO.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否已经秒杀到了
        List<OrderSeckill> orders = orderService.isExist(user.getId(), goodsId);
        if(orders.size()>0){
            return ResultDTO.error(CodeMsg.SECKILL_EXIST);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);//10
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return ResultDTO.error(CodeMsg.SECKILL_OVER);
        }
        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendSeckillMessage(mm);
        return ResultDTO.success(0);//排队中
    }


    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public ResultDTO<Long> seckillResultDTO(Model model,User user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return ResultDTO.error(CodeMsg.SESSION_ERROR);
        }
        long result  =seckillService.getSeckillResult(user.getId(), goodsId);
        return ResultDTO.success(result);
    }

    @AccessLimit(seconds=5, maxCount=5)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public ResultDTO<String> getSeckillPath(HttpServletRequest request, User user,
                                            @RequestParam("goodsId")long goodsId,
                                            @RequestParam(value="verifyCode", defaultValue="0")int verifyCode) {
        if(user == null) {
            return ResultDTO.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check) {
            return ResultDTO.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path  =seckillService.createSeckillPath(user, goodsId);
        return ResultDTO.success(path);
    }


    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public ResultDTO<String> getSeckillVerifyCode(HttpServletResponse response, User user,
                                                  @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return ResultDTO.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = seckillService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return ResultDTO.error(CodeMsg.SECKILL_FAILURE);
        }
    }

    @RequestMapping("/seckill")
    public String buy(Model model, User user, @RequestParam("goodsId")Long goodsId){
        if(user == null) {
            return "login";
        }
        //获取到对应商品
        GoodsExtend goods = goodsService.selectGEById(goodsId);
        //判断库存是否足够
        if(goods.getStockCount()<=0){
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }
        //判断是否已经参与秒杀
        List<OrderSeckill> orders = orderService.isExist(user.getId(), goodsId);
        if(orders.size()>0){
            model.addAttribute("errmsg", CodeMsg.SECKILL_EXIST.getMsg());
            return "seckill_fail";
        }
        OrderInfo orderInfo;
        try {
            //减少库存,生成订单
            orderInfo = seckillService.seckill(user.getId(), goods);
        } catch (Exception e) {
            model.addAttribute("errmsg", e.getMessage());
            return "seckill_fail";
        }
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
    }

    @RequestMapping("/seckillAsync")
    public String buy2(Model model, User user, @RequestParam("goodsId")Long goodsId){
        if(user == null) {
            return "login";
        }
        //获取到对应商品
        GoodsExtend goods = goodsService.selectGEById(goodsId);
        //判断库存是否足够
        if(goods.getStockCount()<=0){
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }
        //判断是否已经参与秒杀
        List<OrderSeckill> orders = orderService.isExist(user.getId(), goodsId);
        if(orders.size()>0){
            model.addAttribute("errmsg", CodeMsg.SECKILL_EXIST.getMsg());
            return "seckill_fail";
        }
        OrderInfo orderInfo=null;
        try {
            //减少库存,生成订单
            seckillService.seckillAsync(user.getId(), goods);
        } catch (Exception e) {
            model.addAttribute("errmsg", e.getMessage());
            return "seckill_fail";
        }
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "seckill_success";
    }
}
