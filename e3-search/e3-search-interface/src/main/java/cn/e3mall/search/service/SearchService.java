package cn.e3mall.search.service;

import cn.e3mall.search.pojo.SearchResult;

public interface SearchService {
	SearchResult search(String keyWord, int page, int rows) throws Exception;

}
