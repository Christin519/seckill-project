package com.czw.service;

import com.czw.bean.Goods;
import com.czw.bean.OrderInfo;
import com.czw.bean.OrderSeckill;
import com.czw.mapper.OrderMapper;
import com.czw.mapper.OrderSeckillMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author: ChengZiwang
 * @date: 2020/7/23
 **/
@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderSeckillMapper orderSeckillMapper;


    //查询秒杀订单是否存在
    public List<OrderSeckill> isExist(Long userId,Long goodsId){
        HashMap<String, Object> param = new HashMap<>();
        param.put("user_id",userId);
        param.put("goods_id",goodsId);
        List<OrderSeckill> orderSeckills = orderSeckillMapper.selectByMap(param);
        return orderSeckills;
    }

    //下单，普通订单和秒杀订单同时更新
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo makeOrder(Long userId, Goods goods){
        //下订单
        OrderInfo orderInfo = OrderInfo.builder()
                .userId(userId)
                .goodsId(goods.getId())
                .goodsName(goods.getGoodsName())
                .goodsPrice(goods.getGoodsPrice())
                .goodsCount(1)
                .createDate(new Date())
                .build();
        orderMapper.insert(orderInfo);
        //获取刚刚的订单id下秒杀订单
        OrderSeckill orderSeckill = OrderSeckill.builder().userId(userId).goodsId(goods.getId()).orderId(orderInfo.getId()).build();
        int i = orderSeckillMapper.insert(orderSeckill);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderMapper.selectById(orderId);
    }
}
