package cn.e3mall.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.search.service.SearchItemService;
import cn.e3mall.search.service.impl.SearchItemServiceImpl;

public class ItemChangeListener implements MessageListener {

	@Autowired
	private SearchItemService searchItemService;

	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = null;
			Long itemId = null;
			// 获取id
			if (message instanceof TextMessage) {
				textMessage = (TextMessage) message;
				String text = textMessage.getText();
				itemId = Long.parseLong(text);
			}
			//向索引库添加文档
			searchItemService.addDocument(itemId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
