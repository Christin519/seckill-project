package com.czw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czw.bean.Goods;
import com.czw.bean.GoodsExtend;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: ChengZiwang
 * @date: 2020/7/22
 **/
public interface GoodsMapper extends BaseMapper<Goods> {

    @Select("select g.*,gs.seckill_price,gs.stock_count,gs.start_date,gs.end_date from goods g left join goods_seckill gs on g.id = gs.goods_id")
    List<GoodsExtend> selectGE();

    @Select("select g.*,gs.seckill_price,gs.stock_count,gs.start_date,gs.end_date " +
            "from goods g left join goods_seckill gs on g.id = gs.goods_id " +
            "where g.id = #{id}")
    GoodsExtend selectGEById(Long id);
}
