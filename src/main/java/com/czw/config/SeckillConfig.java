package com.czw.config;

import com.czw.bean.GoodsExtend;
import com.czw.mapper.GoodsMapper;
import com.czw.redis.GoodsKey;
import com.czw.service.GoodsService;
import com.czw.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ChengZiwang
 * @date: 2020/7/29
 **/
@Component
public class SeckillConfig implements ApplicationRunner {

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    private GoodsService goodsService;

    @Autowired
    RedisService redisService;

    public static Map<Long, Boolean> localOverMap =  new ConcurrentHashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<GoodsExtend> list = goodsService.selectGE();
        list.stream().forEach(i -> test(i.getId()));
    }

    public void test(Long id){
        //查数据库
        GoodsExtend goods = goodsMapper.selectGEById(id);
        //写缓存
        redisService.set(GoodsKey.getGoodsId,id.toString(),goods);
        redisService.set(GoodsKey.getSeckillGoodsStock,""+id,goods.getStockCount());
        localOverMap.put(id,false);
//        //查缓存
//        GoodsExtend goods = redisService.get(GoodsKey.getGoodsId, id.toString(), GoodsExtend.class);
//        if(goods==null) {
//            //查数据库
//            goods = goodsMapper.selectGEById(id);
//            //写缓存
//            redisService.set(GoodsKey.getGoodsId,id.toString(),goods);
//        }
    }
}
