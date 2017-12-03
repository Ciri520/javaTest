package cn.e3mall.search.solrDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.search.pojo.SearchItem;
import cn.e3mall.search.pojo.SearchResult;

@Repository
public class SearchDaoImpl implements SearchDao {

	@Autowired
	private SolrServer solrServer;

	@Override
	public SearchResult search(SolrQuery solrQuery) throws Exception {
		// 根据查询条件查询索引库
		QueryResponse query = solrServer.query(solrQuery);
		// 获得查询结果
		SolrDocumentList results = query.getResults();
		// 获得总记录数
		long numFound = results.getNumFound();
		// 获取商品的高亮显示
		Map<String, Map<String, List<String>>> highlighting = query.getHighlighting();
		List<SearchItem> list = new ArrayList<>();
		// 遍历结果
		for (SolrDocument solrDocument : results) {
			SearchItem searchItem = new SearchItem();
			searchItem.setId((String) solrDocument.get("id"));
			searchItem.setCategory_name((String) solrDocument.get("item_category_name"));
			searchItem.setImage((String) solrDocument.get("item_image"));
			searchItem.setPrice((long) solrDocument.get("item_price"));
			searchItem.setSell_point((String) solrDocument.get("item_sell_point"));
			// 获取高亮结果
			List<String> list2 = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = "";
			if (list2 != null && list2.size() > 0) {
				title = list2.get(0);
			} else {
				title = (String) solrDocument.get("item_title");
			}
			searchItem.setTitle(title);
			list.add(searchItem);
		}
		// 把结果封装到SearchResult中;
		SearchResult searchResult = new SearchResult();
		searchResult.setItemList(list);
		searchResult.setRecordCount(numFound);
		return searchResult;
	}

}
