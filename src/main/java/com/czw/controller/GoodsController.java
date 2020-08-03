package com.czw.controller;

import com.czw.bean.GoodsExtend;
import com.czw.bean.User;
import com.czw.dto.GoodsDetailDTO;
import com.czw.dto.ResultDTO;
import com.czw.redis.GoodsKey;
import com.czw.service.GoodsService;
import com.czw.service.RedisService;
import com.czw.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	UserService userService;

	@Autowired
	GoodsService goodsService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	
    @RequestMapping(value = "/list",produces = "text/html")
	@ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, User user) {
    	model.addAttribute("user", user);
		//取缓存
		String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		List<GoodsExtend> list = goodsService.selectGE();
		model.addAttribute("goodsList",list);
		WebContext ctx = new WebContext(request,response,
				request.getServletContext(),request.getLocale(), model.asMap() );
		//手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		if(!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
    }

	@RequestMapping(value="/{goodsId}")
	@ResponseBody
	public ResultDTO<GoodsDetailDTO> detail(HttpServletRequest request, HttpServletResponse response, Model model,
											@PathVariable("goodsId")long goodsId,User user) {
		GoodsExtend goods = goodsService.selectGEById(goodsId);
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		GoodsDetailDTO vo = new GoodsDetailDTO();
		vo.setGoods(goods);
		vo.setUser(user);
		vo.setRemainSeconds(remainSeconds);
		vo.setMiaoshaStatus(miaoshaStatus);
		return ResultDTO.success(vo);
	}

	@RequestMapping(value = "/detail/{goodsId}",produces = "text/html")
	@ResponseBody
	public String detail(HttpServletRequest request, HttpServletResponse response,
						 Model model,User user, @PathVariable("goodsId")long goodsId) {
		model.addAttribute("user", user);
		//取缓存
		String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		GoodsExtend goods = goodsService.selectGEById(goodsId);
		model.addAttribute("goods", goods);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);
		WebContext ctx = new WebContext(request,response,
				request.getServletContext(),request.getLocale(), model.asMap() );
		//手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if(!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
		}
		return html;
	}
}
