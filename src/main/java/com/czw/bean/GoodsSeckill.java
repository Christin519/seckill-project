package com.czw.bean;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class GoodsSeckill {
	private Long id;
	private Long goodsId;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;

}
