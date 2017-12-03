package cn.e3mall.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class GlobalExceptionReslover implements HandlerExceptionResolver {

	private Logger logger = LoggerFactory.getLogger(GlobalExceptionReslover.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception e) {
		// 写日志文件
		logger.error("系统发生异常",e);
		// 发邮件，发短信,相关责任人
		// Jmail：查找相关资料
		// 需要购买短信，调用第三方接口
		// 展示错误界面
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", "系统正忙，请稍后再试！");
		modelAndView.setViewName("error/exception");
		return modelAndView; 
	}

}
