package cn.e3mall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;

	@Value("${order.id.begin}")
	private String orderIdBegin;

	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		// 1:接受表单数据
		// 2:生成id
		if (!jedisClient.exists("order_id_gen")) {
			// 如果不存在,设置一个初始值
			jedisClient.set("order_id_gen", orderIdBegin);
		}
		String orderId = jedisClient.incr("order_id_gen").toString();
		orderInfo.setOrderId(orderId);
		// 把pojo对象的数据补全
		// 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		// 3:向订单表插入数据。
		orderMapper.insert(orderInfo);
		// 4:向订单明细表中插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			// 页面得到的数据中没有id,这里要生成一个id添加进来
			String orderDetailId = jedisClient.incr("order_detail_id_gen").toString();
			// 补充数据
			tbOrderItem.setId(orderDetailId);
			tbOrderItem.setOrderId(orderId);
			// 插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		// 5:向订单物流表插入数据。
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		// 补充数据
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		// 插入数据
		orderShippingMapper.insert(orderShipping);
		return E3Result.ok(orderId);
	}

}
