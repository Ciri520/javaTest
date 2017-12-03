package cn.e3mall.search.solrDao;

import org.apache.solr.client.solrj.SolrQuery;

import cn.e3mall.search.pojo.SearchResult;

public interface SearchDao {
	SearchResult search(SolrQuery solrQuery)throws Exception;
}
