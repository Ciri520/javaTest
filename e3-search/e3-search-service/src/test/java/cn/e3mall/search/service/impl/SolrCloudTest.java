package cn.e3mall.search.service.impl;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrCloudTest {

	@Test
	public void addDocument() throws Exception {
		// 1.创建一个solrserver对象,应该是CloudSolrServer类创建连接
		// 需要参数zkHost,和zookeeper的地址列表并用","隔开
		CloudSolrServer solrserver = new CloudSolrServer("192.168.25.128:2182,192.168.25.128:2183,192.168.25.128:2184");
		// 2.需要设置defaultCollection属性,指定使用那个collection,如果不设置会报错
		solrserver.setDefaultCollection("collection2");
		// 3.创建一个SolrInputDocument对象
		SolrInputDocument document = new SolrInputDocument();
		// 4.向文档中添加域
		document.addField("item_title", "测试商品");
		document.addField("item_price", "100");
		document.addField("id", "test001");
		// 5.把对象写入文本库
		solrserver.add(document);
		// 6.提交
		solrserver.commit();

	}
}
