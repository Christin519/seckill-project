package com.czw.dto;

import com.czw.bean.GoodsExtend;
import com.czw.bean.User;
import lombok.Data;

/**
 * @author: ChengZiwang
 * @date: 2020/7/31
 **/
@Data
public class GoodsDetailDTO {

    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsExtend goods ;
    private User user;
}
