package cn.e3mall.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.content.service.ContentCatService;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;
//首页展示
@Controller
public class IndexController {
	
	@Value("${indexSilderCid}")
	private long indexSilderCid;
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/index")
	public String showIndex(Model model) {
		List<TbContent> list = contentService.getContentList(indexSilderCid);
		model.addAttribute("ad1List",list);
		return "index";
	}
}
