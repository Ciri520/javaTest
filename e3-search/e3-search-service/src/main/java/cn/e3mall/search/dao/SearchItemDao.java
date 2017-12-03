package cn.e3mall.search.dao;

import java.util.List;

import cn.e3mall.search.pojo.SearchItem;

public interface SearchItemDao {
	List<SearchItem> getItemList();
	SearchItem getItemById(long itemId);
}
