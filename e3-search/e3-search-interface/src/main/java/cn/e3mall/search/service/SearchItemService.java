package cn.e3mall.search.service;

import cn.e3mall.pojo.E3Result;

public interface SearchItemService {
	E3Result importItemList();

	E3Result addDocument(Long itemId) throws Exception;
}
