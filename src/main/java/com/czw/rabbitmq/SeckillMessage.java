package com.czw.rabbitmq;


import com.czw.bean.User;
import lombok.Data;

@Data
public class SeckillMessage {
	private User user;
	private long goodsId;
}
