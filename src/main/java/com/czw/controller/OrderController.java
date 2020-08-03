package com.czw.controller;

import com.czw.bean.GoodsExtend;
import com.czw.bean.OrderInfo;
import com.czw.bean.User;
import com.czw.dto.OrderDetailDTO;
import com.czw.dto.ResultDTO;
import com.czw.enums.CodeMsg;
import com.czw.service.GoodsService;
import com.czw.service.OrderService;
import com.czw.service.RedisService;
import com.czw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: ChengZiwang
 * @date: 2020/7/31
 **/
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public ResultDTO<OrderDetailDTO> info(Model model, User user,
                                       @RequestParam("orderId") long orderId) {
        if(user == null) {
            return ResultDTO.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return ResultDTO.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsExtend goods = goodsService.selectGEById(goodsId);
        OrderDetailDTO vo = new OrderDetailDTO();
        vo.setOrder(order);
        vo.setGoods(goods);
        return ResultDTO.success(vo);
    }
}
