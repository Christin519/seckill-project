package com.czw.redis;

public class GoodsKey extends BasePrefix{

	private GoodsKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static GoodsKey getGoodsId = new GoodsKey(600, "gi");
	public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
	public static GoodsKey getSeckillGoodsStock = new GoodsKey(600, "gs");
	public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
}
