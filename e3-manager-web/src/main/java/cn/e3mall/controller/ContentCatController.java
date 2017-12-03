package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.content.service.ContentCatService;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.EasyUITreeNode;


/**
 * 内容分类管理Controller
 * <p>Title: ContentCatController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Controller
public class ContentCatController {
	
	@Autowired
	private ContentCatService contentCatService;

	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCatList(@RequestParam(value="id", defaultValue="0") long parentId) {
		List<EasyUITreeNode> list = contentCatService.getContentCatList(parentId);
		return list;
	}
	
	@RequestMapping(value="/content/category/create", method=RequestMethod.POST)
	@ResponseBody
	public E3Result addContentCategory(Long parentId, String name) {
		E3Result e3Result = contentCatService.addContentCategory(parentId, name);
		return e3Result;
	}
	
	@RequestMapping(value="/content/category/update")
	public E3Result updataeContentCategory(Long id, String name) {
		E3Result e3Result = contentCatService.updataeContentCategory(id, name);
		return e3Result.ok();
	}
	
	@RequestMapping(value="/content/category/delete")
	public @ResponseBody E3Result deleteContentCategory(Long id) {
		E3Result e3Result = contentCatService.deleteContentCategory(id);
		return e3Result;
	}
}
