package com.czw.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSeckill {
	private Long id;
	private Long userId;
	private Long  orderId;
	private Long goodsId;
}
