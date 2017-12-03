package cn.e3mall.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 监听商品添加生成静态页面
 * 
 * @author zqz
 *
 */
public class ItemAddMessageListener implements MessageListener {
	
	@Autowired
	private ItemService itemService;
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Value("${html.gen.path}")
	private String htmlGehPath;

	@Override
	public void onMessage(Message message) {
		try {
			// 1、从消息中取商品id
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
//			Long itemId = new Long(text);
			long itemId = Long.parseLong(text);
			// 系统延时
			Thread.sleep(1000);
			// 2、根据商品id取商品基本信息和商品描述。
			TbItem tbItem = itemService.getItemById(itemId);
			Item item = new Item(tbItem);
			TbItemDesc tbItemDesc = itemService.getItemDescById(itemId);
			// 3、加载freemarker模板
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			// 4、创建数据集
			Map dataModel = new HashMap<>();
			dataModel.put("item", item);
			dataModel.put("itemDesc", tbItemDesc);
			// 5、生成静态页面，输出到任意目录。
			Writer out = new FileWriter(new File(htmlGehPath+itemId+".html"));
			template.process(dataModel, out);
			out.close();
			// 6、配置spring整合activemq，配置消息监听。
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
