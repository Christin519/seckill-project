package com.czw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czw.bean.OrderInfo;

/**
 * @author: ChengZiwang
 * @date: 2020/7/23
 **/
public interface OrderMapper extends BaseMapper<OrderInfo> {

    void insertSeckill();
    void insertOrder();
}
