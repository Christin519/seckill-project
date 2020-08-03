package com.czw.dto;

import com.czw.bean.GoodsExtend;
import com.czw.bean.OrderInfo;
import lombok.Data;

/**
 * @author: ChengZiwang
 * @date: 2020/7/31
 **/
@Data
public class OrderDetailDTO {

    private GoodsExtend goods;
    private OrderInfo order;
}
