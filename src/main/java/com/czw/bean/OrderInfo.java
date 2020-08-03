package com.czw.bean;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Builder
public class OrderInfo {
	private Long id;
	private Long userId;
	private Long goodsId;
	private Long  deliveryAddrId;//收获地址
	private String goodsName;
	private Integer goodsCount;//数量
	private Double goodsPrice;
	private Integer orderChannel;//订单渠道，1在线，2android，3ios
	private Integer status;//订单状态，0未支付，1已支付，2已发货，3已收货，4已退款，5已完成
	private Date createDate;
	private Date payDate;
}
