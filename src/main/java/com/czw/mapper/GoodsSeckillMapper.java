package com.czw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czw.bean.GoodsSeckill;
import org.apache.ibatis.annotations.Update;

/**
 * @author: ChengZiwang
 * @date: 2020/7/23
 **/
public interface GoodsSeckillMapper extends BaseMapper<GoodsSeckill> {

    @Update("update goods_seckill set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    int updateStock(Long goodsId);
}
