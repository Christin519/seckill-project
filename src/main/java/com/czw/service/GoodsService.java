package com.czw.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czw.bean.Goods;
import com.czw.bean.GoodsExtend;
import com.czw.mapper.GoodsMapper;
import com.czw.mapper.GoodsSeckillMapper;
import com.czw.redis.GoodsKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: ChengZiwang
 * @date: 2020/7/22
 **/
@Service
public class GoodsService extends ServiceImpl<GoodsMapper, Goods> {

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsSeckillMapper goodsSeckillMapper;

    @Autowired
    RedisService redisService;

    public List<GoodsExtend> selectGE(){
        return goodsMapper.selectGE();
    }

    public GoodsExtend selectGEById(Long id){
        //查缓存
        GoodsExtend goods = redisService.get(GoodsKey.getGoodsId, id.toString(), GoodsExtend.class);
        if(goods==null) {
            //查数据库
            goods = goodsMapper.selectGEById(id);
            //写缓存
            redisService.set(GoodsKey.getGoodsId,id.toString(),goods);
        }
        return goods;
    }


    public int updateStock(GoodsExtend goods){
        //更新库存
        return goodsSeckillMapper.updateStock(goods.getId());

//        goods.setStockCount(goods.getStockCount() - 1);
//            redisService.set(GoodsKey.getGoodsId, "" + goods.getId(), goods);
//        redisUtil.del(GoodsKey.getGoodsId+""+goods.getId());
//        int i=0;
//        ReentrantLock lock = new ReentrantLock();
//        lock.lock();
//        try {
//            //更新数据库
//            i=goodsSeckillMapper.updateStock(goods.getId());
//            //删除缓存
//            redisUtil.del(GoodsKey.getGoodsId+""+goods.getId());
//        }finally {
//            lock.unlock();
//        }
//        if(i==0){
//            throw new GlobalException(CodeMsg.SECKILL_OVER);
//        }
    }
}
