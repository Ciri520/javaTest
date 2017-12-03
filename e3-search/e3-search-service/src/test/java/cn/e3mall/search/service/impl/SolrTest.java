package cn.e3mall.search.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SolrTest {
	// 添加
	@Test
	public void addDocument() throws Exception {
		// 1:把solr的jar包添加到工程中
		// 2:创建一个solrServer对象,使用httpSolrServer对象创建
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
		// 3:创建一个文档对象solrInputDocument
		SolrInputDocument document = new SolrInputDocument();
		// 4:向文档对象中添加域,必须有id域,域的名称必须在schema.xml中定义
		document.addField("id", "test001");
		document.addField("item_title", "测试商品");
		document.addField("item_price", "998");
		// 5:把文档对象添加到索引库
		solrServer.add(document);
		// 6:提交
		solrServer.commit();
	}

	// 根据查询删除
	@Test
	public void deleteDocumentByQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
		solrServer.deleteByQuery("item_title:测试商品");
		solrServer.commit();
	}

	// 简单查询
	@Test
	public void queryDocument() throws Exception {
		// 1:创建solrServer对象
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
		// 2:创建一个solrQuery对象
		SolrQuery query = new SolrQuery();
		// 3:向solrQuery对象添加查询条件
		query.set("q", "*:*");
		// 4:执行查询,得到一个Respones对象
		QueryResponse response = solrServer.query(query);
		// 5:获取查询结果
		SolrDocumentList results = response.getResults();
		System.out.println("查询的总记录数为:" + results.getNumFound());
		// 6:遍历打印结果
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_price"));
		}
	}

	// 高亮显示查询
	@Test
	public void queryDocumentWithHighLighting() throws Exception {
		// 1:创建一个httpSolrServer对象
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
		// 2:创建一个solrQuery对象
		SolrQuery query = new SolrQuery();
		// 3:向solrQuery中添加条件
		// query.set("q", "测试");
		query.setQuery("测试");
		// 指定搜索域
		query.set("df", "item_keywords");
		// 开启高亮显示
		query.setHighlight(true);
		// 高亮显示域
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("<em/>");
		// 4:执行查询,得到response对象
		QueryResponse response = solrServer.query(query);
		// 5:获取查询结果
		SolrDocumentList results = response.getResults();
		System.out.println("查询的总记录数为:" + results.getNumFound());
		// 6:遍历打印结果
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			// 获取高亮显示
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(solrDocument.get("id")).get(solrDocument.get("item_title"));
				String item_title = null;
			if(list!=null&&list.size()>0){
				item_title = list.get(0);
			}else{
				item_title = (String) solrDocument.get("item_title");
			}
			System.out.println(item_title);
			System.out.println(solrDocument.get("item_price"));
		}
	}

}
