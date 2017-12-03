package cn.e3mall.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
public class HtmlGenController {

	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer; 
	
	@RequestMapping("/genhtml")
	@ResponseBody
	public String genHtml()throws Exception{
		// 1:从string容器中获取FreeMarkerConfigurer
		// 2:从FreeMarkerConfigurer中获取configuration
		Configuration configuration = freeMarkerConfigurer.getConfiguration();
		// 3:从configuration获取template
		Template template = configuration.getTemplate("hello.ftl");
		// 4:创建数据集,并添加数据
		Map dataModel = new HashMap<>(); 
		dataModel.put("hello", "spring-freemarker");
		// 5:创建writer输出对象
		FileWriter out = new FileWriter(new File("D:/temp/out/hello.html"));
		// 6:执行template的process方法,生成文件
		template.process(dataModel, out);
		// 7:关闭流
		out.close();
		return "ok";
	}
}
