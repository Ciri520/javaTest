package cn.e3mall.sso.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.remoting.exchange.Request;

import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;
import cn.e3mall.utils.CookieUtils;
import cn.e3mall.utils.JsonUtils;

/**
 * 用户注册的controller
 * 
 * @author zqz
 *
 */
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping("/page/register")
	public String showRegister() throws Exception {
		return "register";
	}

	@RequestMapping("/page/login")
	public String showLogin(String redirectUrl,Model model) throws Exception {
		model.addAttribute("redirect", redirectUrl);
		return "login";
	}

	@RequestMapping("/user/check/{data}/{type}")
	@ResponseBody
	public E3Result checkData(@PathVariable String data, @PathVariable Integer type) {
		E3Result result = userService.checkData(data, type);
		return result;
	}

	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	@ResponseBody
	public E3Result register(TbUser user) {
		E3Result result = userService.createUser(user);
		return result;
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	@ResponseBody
	public E3Result login(String username, String password, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 1、接收两个参数。
		// 2、调用Service进行登录。
		E3Result e3Result = userService.login(username, password);
		// 判断是否登陆成功
		if(e3Result.getStatus()==200){
			// 3、从返回结果中取token，写入cookie。Cookie要跨域。
			String token = e3Result.getData().toString();
			// 把token设置到cookie中
			CookieUtils.setCookie(request, response, "token", token);
		}
		return e3Result;
	}
	
	@RequestMapping(value="/user/token/{token}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callback){
		E3Result e3Result = userService.getUserByToken(token);
		//判断是否是jsonp请求
		if(StringUtils.isNotBlank(callback)){
			return callback+"("+JsonUtils.objectToJson(e3Result)+")";
		}
		return JsonUtils.objectToJson(e3Result);
	}
	
	@RequestMapping("user/logout/{token}")
	public String logout(@PathVariable String token){
		E3Result e3Result = userService.logout(token);
		return "redirect:http://localhost:8084/";
	}
}
