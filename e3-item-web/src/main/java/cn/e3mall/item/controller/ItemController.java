package cn.e3mall.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;

/**
 * 商品详情页面controller
 * @author zqz
 *
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	public String showItemInfo(@PathVariable Long itemId, Model model) {
		//根据商品id取商品基本信息
		TbItem tbItem = itemService.getItemById(itemId);
		//基于TbItme初始化Item对象
		Item item = new Item(tbItem);
		//根据商品id 取商品描述
		TbItemDesc tbItemDesc = itemService.getItemDescById(itemId);
		//把数据传递给jsp
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", tbItemDesc);
		//返回路径视图
		return "item";
	}
}
