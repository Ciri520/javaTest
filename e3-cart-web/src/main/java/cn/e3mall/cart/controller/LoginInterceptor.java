package cn.e3mall.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;
import cn.e3mall.sso.service.UserService;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.JsonUtils;
/**
 *  判断用户是否登录的拦截器
 * @author zqz
 *
 */
public class LoginInterceptor implements HandlerInterceptor {
	
	@Autowired
	private UserService userService;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 1、实现一个HandlerInterceptor接口。
		// 2、在执行handler方法之前做业务处理
		// 3、从cookie中取token。使用CookieUtils工具类实现。
		String token = CookieUtils.getCookieValue(request, "token", true);
		// 4、没有取到token，用户未登录。放行
		if(StringUtils.isBlank(token)){
			return true;
		}
		// 5、取到token，调用sso系统的服务，根据token查询用户信息。
		if(StringUtils.isNotBlank(token)){
			E3Result result = userService.getUserByToken(token);
			// 用户信息已经过期
			if(result.getStatus()!=200){
				return true;
			}else {
				// 存在用户
				TbUser user = (TbUser) result.getData();
				request.setAttribute("user", user);
			}
		}
		return true;
	}

}
