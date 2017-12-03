package cn.e3mall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.JsonUtils;

public class LoginInterceptor implements HandlerInterceptor {

	@Value("${sso.login.url}")
	private String loginUrl;
	@Autowired
	private UserService userService;
	@Autowired
	private CartService cartService;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 从cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		// 如果token不存在
		if (StringUtils.isBlank(token)) {
			// 从request中获取将要访问的url
			StringBuffer url = request.getRequestURL();
			// 跳转到登陆页面并把需要访问的网址传递过去
			response.sendRedirect(loginUrl + "?redirectUrl=" + url);
			return false;
		}
		// 如果存在
		// 根据token从redis中查询用户信息
		E3Result e3Result = userService.getUserByToken(token);
		// 如果不存在,用户信息已经过期,需要从新登陆,继续跳转到登陆页面并把需要访问的网址传递过去
		if (e3Result.getStatus() != 200) {
			StringBuffer url = request.getRequestURL();
			// 跳转到登陆页面并把需要访问的网址传递过去
			response.sendRedirect(loginUrl + "?redirectUrl=" + url);
			return false;
		}
		// 如果取到用户信息，用户已经是登录状态，把用户信息保存到request中。放行
		TbUser user = (TbUser) e3Result.getData();
		request.setAttribute("user", user);
		// 判断购物车里是否存在 物品
		String jsonList = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isNoneBlank(jsonList)) {
			// 合并购物车
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(jsonList, TbItem.class));
			// 合并完成之后删除cookie中的购物车
			CookieUtils.deleteCookie(request, response, "cart");
		}
		// 放行
		return true;
	}
}
