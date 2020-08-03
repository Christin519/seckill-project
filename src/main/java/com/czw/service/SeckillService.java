package com.czw.service;

import com.czw.bean.*;
import com.czw.enums.CodeMsg;
import com.czw.exception.GlobalException;
import com.czw.redis.GoodsKey;
import com.czw.redis.SeckillKey;
import com.czw.util.MD5Util;
import com.czw.util.UUIDUtil;
import com.czw.util.VerifyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author: ChengZiwang
 * @date: 2020/7/23
 **/
@Service
public class SeckillService {

    @Autowired
    private OrderService orderService;

    @Resource
    private GoodsService goodsService;

    @Resource
    RedisService redisService;

    //下单，普通订单和秒杀订单同时更新
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo seckill(Long userId, GoodsExtend goods){
        //更新秒杀商品库存
        int i = goodsService.updateStock(goods);
        if(i==0){
            setGoodsOver(goods.getId());
            return null;
        }
        //删除缓存
//        redisService.delete(GoodsKey.getGoodsId,goods.getId().toString());
        //下订单
        OrderInfo orderInfo = orderService.makeOrder(userId,goods);
        return orderInfo;
    }

    public long getSeckillResult(Long userId, long goodsId) {
        List<OrderSeckill> orders = orderService.isExist(userId, goodsId);
        if(orders.size() > 0) {
            //秒杀成功
            return orders.get(0).getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }


    public boolean checkPath(User user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createSeckillPath(User user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, str);
        return str;
    }

    public BufferedImage createVerifyCode(User user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        HashMap<String, Object> map = VerifyCode.createVerifyCode();
        Integer verifyCode = (Integer) map.get("code");
        BufferedImage image = (BufferedImage) map.get("image");
        //把验证码存到redis中
        redisService.set(SeckillKey.getSeckillVerifyCode, user.getId()+","+goodsId, verifyCode);
        //输出图片
        return image;
    }

    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(SeckillKey.getSeckillVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(SeckillKey.getSeckillVerifyCode, user.getId()+","+goodsId);
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public void seckillAsync(Long userId, GoodsExtend goods) throws Exception {
        //更新秒杀商品库存
        int i = goods.getStockCount();
        if(i<=0){
            throw new GlobalException(CodeMsg.SECKILL_OVER);
        }
        goods.setStockCount(i-1);
        //更新缓存
        redisService.set(GoodsKey.getGoodsId,""+goods.getId(),goods);
        //异步减库存，下订单
        taskOrder(userId,goods);
        taskReduce(userId,goods);
    }

    @Async
    public Future<OrderInfo> taskOrder(Long userId, GoodsExtend goods){
        //下订单
        OrderInfo orderInfo = orderService.makeOrder(userId,goods);
        return new AsyncResult<OrderInfo>(orderInfo);
    }

    @Async
    public Future<Integer> taskReduce(Long userId, GoodsExtend goods){
        //更新秒杀商品库存db
        int i = goodsService.updateStock(goods);
        return new AsyncResult<Integer>(i);
    }
}
