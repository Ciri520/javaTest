package cn.e3mall.service;

import cn.e3mall.pojo.DataGridResult;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;

public interface ItemService {
	TbItem getItemById(long itemId);
	public DataGridResult getItemListPage(int page, int rows);
	E3Result addItem(TbItem item, String desc);
}
