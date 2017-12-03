package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * 购物车管理controller
 * @author zqz
 *
 */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.JsonUtils;

@Controller
public class CartController {

	@Autowired
	private ItemService itemService;
	@Value("${cookie.cart.expire}")
	private Integer cartExpire;
	@Autowired
	private CartService cartService;

	// 获取购物车信息
	private List<TbItem> getCartList(HttpServletRequest request) {
		// 从cookie中获取购物车信息
		String cookieValue = CookieUtils.getCookieValue(request, "cart", true);
		// 如果有把String对象转换成pojo
		if (StringUtils.isNotBlank(cookieValue)) {
			List<TbItem> list = JsonUtils.jsonToList(cookieValue, TbItem.class);
			return list;
		}
		return new ArrayList<>();
	}

	@RequestMapping("/cart/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId, Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		// 判断用户是否登陆
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			// 如果登陆取用户id
			Long id = user.getId();
			// 把商品添加到服务器
			E3Result e3Result = cartService.addCart(user.getId(), itemId, num);
			return "cartSuccess";
		}
		// 不存在
		// 从cookie中查询商品信息
		List<TbItem> cartList = getCartList(request);
		boolean hasItem = false;
		for (TbItem tbItem : cartList) {
			// 包装类型比较的是地址值.把其中的一个对象转换成数值类型即可
			// 如果购物车中有该商品数量加一.
			if (tbItem.getId().longValue() == itemId) {
				tbItem.setNum(tbItem.getNum() + num);
				hasItem = true;
				break;
			}
		}
		if (!hasItem) {
			// 如果没有,从数据库查询商品信息并添加到Cookie
			TbItem item = itemService.getItemById(itemId);
			// 只取一张照片
			String image = item.getImage();
			if (StringUtils.isNoneBlank(image)) {
				item.setImage(image.split(",")[0]);
			}
			// 设置数量
			item.setNum(num);
			// 把商品添加到集合
			cartList.add(item);
		}
		// 把购物车写入cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), cartExpire, true);
		return "cartSuccess";
	}

	@RequestMapping("cart/cart")
	public String showCartList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		// 从cookie中取购物车列表
		List<TbItem> list = getCartList(request);
		// 判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			// 合并购物车
			if (list.size() > 0) {
				cartService.mergeCart(user.getId(), list);
				// 删除cookie中的购物车数据
				CookieUtils.deleteCookie(request, response, "cart");
			}
			// 取购物车列表
			list = cartService.getCartList(user.getId());
		}
		// 2）把列表传递给jsp
		request.setAttribute("cartList", list);
		return "cart";
	}

	// request, response 只能运用在表现层  不能传递到后台
	
	
	// @RequestMapping("cart/cart")
	// public String showCartList(HttpServletRequest request,
	// HttpServletResponse response, Model model) throws Exception {
	// // 判断是否为登陆状态 Object object = request.getAttribute("user");
	// if (object != null) {
	// // 登陆状态
	// TbUser user = (TbUser) object;
	// // 从服务器查询购物车信息
	// List<TbItem> list = cartService.getCartList(user, request, response);
	// model.addAttribute("cartList", list);
	// return "cart";
	// }
	// // 从Cookie中查询购物车 List<TbItem> list = getCartList(request);
	// // 传递给页面
	// model.addAttribute("cartList", list);
	// return "cart";
	// }

	// Serialized class org.apache.catalina.connector.RequestFacade must
	// implement java.io.Serializable

	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateNum(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		// 判断是否为登录状态
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object;
			// 更新服务端的购物车
			cartService.updateCartItemNum(user.getId(), itemId, num);
			return E3Result.ok();
		}
		// 从Cookie中查询购物车
		List<TbItem> list = getCartList(request);
		// 遍历集合
		for (TbItem tbItem : list) {
			// 找到对应的商品
			if (tbItem.getId().longValue() == itemId) {
				// 修改num
				tbItem.setNum(num);
				break;
			}
		}
		// 把修改后的商品集合加入到集合中
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), true);
		// 相应json数据
		return E3Result.ok();
	}

	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId, HttpServletRequest request, HttpServletResponse response) {
		// 判断用户登录状态
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object;
			// 删除服务端的购物车商品
			cartService.deleteCartItem(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		// 从cookie中获取购物车信息
		List<TbItem> cartList = getCartList(request);
		// 遍历集合
		for (TbItem tbItem : cartList) {
			// 寻找对应的商品
			if (tbItem.getId().longValue() == itemId) {
				// 删除该商品
				cartList.remove(tbItem);
				break;
			}
		}
		// 把该商品放入cookie中
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), cartExpire, true);
		// 返回逻辑视图.因为要刷新页面,所以采用重定向
		return "redirect:/cart/cart.html";
	}

}
