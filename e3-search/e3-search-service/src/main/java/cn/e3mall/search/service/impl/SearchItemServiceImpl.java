package cn.e3mall.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.pojo.E3Result;
import cn.e3mall.search.dao.SearchItemDao;
import cn.e3mall.search.pojo.SearchItem;
import cn.e3mall.search.service.SearchItemService;

@Service
public class SearchItemServiceImpl implements SearchItemService {
	
	@Autowired
	private SearchItemDao searchItemDao;
	
	@Autowired
	private SolrServer solrServer;
	
	@Override
	public E3Result importItemList() {
		// 查询商品列表
		List<SearchItem> itemList = searchItemDao.getItemList();
		try {
			// 导入索引库
			for (SearchItem searchItem : itemList) {
				// 创建文档对象
				SolrInputDocument document = new SolrInputDocument();
				// 向文档对象中添加域
				document.addField("id", searchItem.getId());
				document.addField("item_title", searchItem.getTitle());
				document.addField("item_sell_point", searchItem.getSell_point());
				document.addField("item_price", searchItem.getPrice());
				document.addField("item_image", searchItem.getImage());
				document.addField("item_category_name", searchItem.getCategory_name());
				// 写入索引库
				solrServer.add(document);
			}
			// 提交
			solrServer.commit();
			// 返回成功
			return E3Result.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return E3Result.build(500, "导入商品失败");
		}
	}
	@Override
	public E3Result addDocument(Long itemId)throws Exception {
		// 1、根据商品id查询商品信息。
		SearchItem searchItem = searchItemDao.getItemById(itemId);
		// 2、创建一SolrInputDocument对象。
		SolrInputDocument document = new SolrInputDocument();
		// 3、使用SolrServer对象写入索引库。
		document.addField("id", searchItem.getId());
		document.addField("item_title", searchItem.getTitle());
		document.addField("item_sell_point", searchItem.getSell_point());
		document.addField("item_price", searchItem.getPrice());
		document.addField("item_image", searchItem.getImage());
		document.addField("item_category_name", searchItem.getCategory_name());
		// 5、向索引库中添加文档。
		solrServer.add(document);
		solrServer.commit();
		// 4、返回成功，返回e3Result。
		return E3Result.ok();
	}

}
