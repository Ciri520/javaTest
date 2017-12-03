package cn.e3mall.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.search.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;
import cn.e3mall.search.solrDao.SearchDao;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;

//	@Value("${DEFAULT_FIELD}")
//	private String DEFAULT_FIELD;

	@Override
	public SearchResult search(String keyWord, int page, int rows) throws Exception {
		// 创建一个SolrQuery对象
		SolrQuery query = new SolrQuery();
		// 设置查询条件
		query.setQuery(keyWord);
		// 设置分页条件
		query.setStart((page - 1) * rows);
		// 设置rows
		query.setRows(rows);
		// 设置搜索域
		query.set("df", "item_keywords");
		// 设置高亮显示
		// 开启高亮显示
		query.setHighlight(true);
		// 设置高亮域
		query.addHighlightField("item_title");
		// 设置前缀
		query.setHighlightSimplePre("<em style=\"color:red\">");
		// 设置后缀
		query.setHighlightSimplePost("</em>");
		// 执行查询
		SearchResult searchResult  = searchDao.search(query);
		//计算总页数
		int count = (int) searchResult.getRecordCount();
		int pages = count/rows;
		if(count%rows>0) page++;
		searchResult.setPageCount(pages);
		return searchResult;
	}

}
