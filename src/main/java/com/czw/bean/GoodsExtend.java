package com.czw.bean;

import com.czw.bean.Goods;
import lombok.Data;

import java.util.Date;

/**
 * @author: ChengZiwang
 * @date: 2020/7/22
 **/
@Data
public class GoodsExtend extends Goods {
    private Long seckillId;
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
