package cn.e3mall.content.service;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.jedis.JedisClient;

public class JedisClientTest {
	
	@Test
	public void testJedisClient()throws Exception{
		// 1_初始化spring容器
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		// 2_获取bean
		JedisClient jedisClient = context.getBean(JedisClient.class);
		// 3_执行方法
		jedisClient.set("testone", "xboxonex");
		String string = jedisClient.get("testone");
		System.out.println(string);
	}
}
