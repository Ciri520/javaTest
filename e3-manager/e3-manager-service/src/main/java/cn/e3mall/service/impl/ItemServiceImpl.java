package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.DataGridResult;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.service.ItemService;
import cn.e3mall.utils.IDUtils;
import cn.e3mall.utils.JsonUtils;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;
	@Value("${redis.item.expire}")
	private Integer redisItemExpire;

	@Override
	public TbItem getItemById(long itemId) {
		try {
			// 先查询缓存
			String json = jedisClient.get("item_info" + itemId + "base");
			if (StringUtils.isNotBlank(json)) {
				// 把json转换成pojo
				TbItem tbItem2 = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem2;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 把查询的结果添加到缓存
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		try {
			// 把pojo转换成json字符串
			String item = JsonUtils.objectToJson(tbItem);
			jedisClient.set("item_info" + itemId + "base", item);
			// 设置过期时间
			jedisClient.expire("item_info" + itemId + "base", redisItemExpire);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItem;
	}

	@Override
	public DataGridResult getItemListPage(int page, int rows) {
		// 1)设置分页信息
		PageHelper.startPage(page, rows);
		// 2)执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		// 3)取查询结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		long total = pageInfo.getTotal();
		// 4)把查询结果封装到DataGridResult
		DataGridResult result = new DataGridResult();
		result.setRows(list);
		result.setTotal(total);
		// 5)返回结果
		return result;
	}

	@Override
	public E3Result addItem(TbItem item, String desc) {
		// 1、生成商品id
		final long itemId = IDUtils.genItemId();
		// 2、补全TbItem对象的属性
		item.setId(itemId);
		// 商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		Date date = new Date();
		item.setUpdated(date);
		item.setCreated(date);
		// 3、向商品表插入数据
		itemMapper.insert(item);
		// 4、创建一个TbItemDesc对象
		TbItemDesc tbItemDesc = new TbItemDesc();
		// 5、补全TbItemDesc的属性
		tbItemDesc.setCreated(date);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setUpdated(date);
		tbItemDesc.setItemId(itemId);
		// 6、向商品描述表插入数据
		itemDescMapper.insert(tbItemDesc);
		// 发送一个商品添加消息
		jmsTemplate.send(topicDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage(itemId + "");
				return message;
			}
		});

		// 7、E3Result.ok()
		return E3Result.ok();
	}

	@Override
	public TbItemDesc getItemDescById(Long itemId) {
		// 查询缓存
		String json = jedisClient.get("item_info:" + itemId + ":desc");
		if (StringUtils.isNoneBlank(json)) {
			// 返回结果
			return JsonUtils.jsonToPojo(json, TbItemDesc.class);
		}
		// 查询数据库
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		try {
			// 加入缓存
			jedisClient.set("item_info:" + itemId + ":desc", JsonUtils.objectToJson(itemDesc));
			// 设置过期时间
			jedisClient.expire("item_info:" + itemId + ":desc", redisItemExpire);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}

}
