package com.czw;

import com.czw.bean.GoodsExtend;
import com.czw.mapper.UserMapper;
import com.czw.redis.GoodsKey;
import com.czw.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootTest
class SpringbootRedisApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisService redisService;

    @Test
    void testDel(){
        GoodsExtend goodsExtend = redisService.get(GoodsKey.getGoodsId, "1", GoodsExtend.class);
        redisService.delete(GoodsKey.getGoodsId,"1");
        System.out.println(goodsExtend);
    }

    @Test
    void contextLoads() {
        Object o = userMapper.selectById(13365852567L);
        System.out.println(o.toString());
    }

    @Test
    void testDate() throws ParseException {
        System.out.println(LocalDateTime.now());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = sdf.format(new Date());
        System.out.println(s);
        String s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        sdf.parse(s1);
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now2 = now.format(dateTimeFormatter);
        System.out.println(LocalDateTime.parse(now2,dateTimeFormatter));
        System.out.println("---------------------------------------");
        long millis = System.currentTimeMillis();
        System.out.println(millis);
        Date timestamp = new Timestamp(millis);
        System.out.println(timestamp);

        System.out.println("--------------------------------");
        Date date = new Date();
        System.out.println(date.getTime());

        System.out.println(LocalDateTime.now());
    }

}
