package cn.e3mall.content.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {

	// 链接单机版Redis
	@Test
	public void testJedis() throws Exception {
		// 1_创建Jedis,需要指定服务器的ip和端口
		Jedis jedis = new Jedis("192.168.25.128", 6379);
		// 2_使用Jedis操作数据库
		jedis.set("test", "jedis test");
		String string = jedis.get("test");
		// 3_打印结果
		System.out.println(string);
		// 4_关闭资源
		jedis.close();
	}

	// 使用链接池 链接单机版Redis
	@Test
	public void testJedisPool() throws Exception {
		// 1_创建一个jedisPool对象,需要指定ip和port
		JedisPool jedisPool = new JedisPool("192.168.25.128", 6379);
		// 2_从jedispool中取jedis对象
		Jedis jedis = jedisPool.getResource();
		// 3_使用jedis操作数据库
		String string = jedis.get("test");
		System.out.println(string);
		// 4_关闭jedis
		jedis.close();
		// 5_关闭jedisPool
		jedisPool.close();
	}
	
	//连接集群版的Redis
	@Test
	public void testJedisCluster()throws Exception{
		// 1_创建一个jedisCluster对象,需要一个Set<HostAndPort>参数,Redis节点列表
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("192.168.25.128", 7001));
		nodes.add(new HostAndPort("192.168.25.128", 7002));
		nodes.add(new HostAndPort("192.168.25.128", 7003));
		nodes.add(new HostAndPort("192.168.25.128", 7004));
		nodes.add(new HostAndPort("192.168.25.128", 7005));
		nodes.add(new HostAndPort("192.168.25.128", 7006));
		JedisCluster jedisCluster = new JedisCluster(nodes);
		// 2_直接使用jedisCluster管理jeids对象
		jedisCluster.set("testone", "one");
		String result = jedisCluster.get("testone");
		System.out.println(result);
		// 3_系统结束之前关闭掉jedisCluster对象
		jedisCluster.close();
	}
}
