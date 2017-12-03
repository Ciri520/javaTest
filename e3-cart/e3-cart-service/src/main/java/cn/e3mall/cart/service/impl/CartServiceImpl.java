package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.JsonUtils;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbItemMapper itemMapper;

	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		// 先从购物车查询是否存在该商品
		Boolean flag = jedisClient.hexists("cart_info:" + userId, itemId + "");
		// 如果存在把数量相加,然后再存入redis
		if (flag) {
			String tbItem = jedisClient.hget("cart_info:" + userId, itemId + "");
			TbItem item = JsonUtils.jsonToPojo(tbItem, TbItem.class);
			item.setNum(item.getNum() + num);
			jedisClient.hset("cart_info:" + userId, itemId + "", JsonUtils.objectToJson(item));
			return E3Result.ok();
		}

		// 如果不存在,需查询商品信息然后存入redis中
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		// 设置商品数量
		tbItem.setNum(num);
		// 取一张照片
		tbItem.setImage(tbItem.getImage().split(",")[0]);
		// 存入redis中
		jedisClient.hset("cart_info:" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		return E3Result.ok();
	}

	// 合并购物车
	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
		// 遍历商品列表
		for (TbItem tbItem : itemList) {
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		return E3Result.ok();
	}

	@Override
	public List<TbItem> getCartList(long userId) {
		// 从redis中根据用户id查询商品列表
		List<String> strList = jedisClient.hvals("cart_info:" + userId);
		List<TbItem> resultList = new ArrayList<>();
		// 把json列表转换成TbItem列表
		for (String string : strList) {
			TbItem tbItem = JsonUtils.jsonToPojo(string, TbItem.class);
			// 添加到列表
			resultList.add(tbItem);
		}
		return resultList;
	}

	@Override
	public E3Result updateCartItemNum(long userId, long itemId, int num) {
		// 从redis中取商品信息
		String json = jedisClient.hget("cart_info:" + userId, itemId + "");
		// 转换成java对象
		TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
		// 更新数量
		tbItem.setNum(num);
		// 写入redis
		jedisClient.hset("cart_info:" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		return E3Result.ok();
	}
	
	@Override
	public E3Result deleteCartItem(long userId, long itemId) {
		// 根据商品id删除hash中对应的商品数据。
		jedisClient.hdel("cart_info:" + userId, itemId + "");
		return E3Result.ok();
	}

	// @Override
	// public List<TbItem> getCartList(TbUser user, HttpServletRequest request,
	// HttpServletResponse response) {
	// // 先从cookie中获取购物车信息 String
	// cookieValue = CookieUtils.getCookieValue(request, "cart", true);
	// List<TbItem> list = JsonUtils.jsonToList(cookieValue, TbItem.class);
	// // 遍历购物车信息
	// for (TbItem tbItem : list) {
	// // 把cookie中的商品添加到redis中(合并购物车)
	// addCart(user.getId(), tbItem.getId(), tbItem.getNum());
	// } // 删除cookie中的信息
	// CookieUtils.deleteCookie(request, response, "cart");
	// // 从redis中获取购物车信息
	// List<String> strList = jedisClient.hvals("cart_info:" + user.getId());
	// List<TbItem> resultList = new ArrayList<>();
	// for (String string : strList) {
	// resultList.add(JsonUtils.jsonToPojo(string, TbItem.class));
	// }
	// return resultList;
	// }

}
