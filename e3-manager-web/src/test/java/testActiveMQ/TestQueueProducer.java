package testActiveMQ;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestQueueProducer {

	@Test
	public void testQueueProducer() throws Exception {
		// 第一步：创建一个connectionFactory对象，需要指定服务器的ip和端口号
		// brokerURL服务器的ip及端口号
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 第二步：使用ConnectionFactory对象创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 第三步：开启连接，调用connection对象的start方法
		connection.start();
		// 第四步：使用connection对象创建一个session对象
		// 第一个参数：是否开启事务，true开启事务之后，第二个参数可以忽略
		// 第二个参数：当第一个参数为false时，才有意义。
		// 消息的应答模式，1.自动应答2.手动应答，一般是自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 第五步：使用session创建一个destination对象(topic,queue)此处创建一个queue对象
		// 参数：队列的名称
		Queue queue = session.createQueue("test-queue");
		// 第六步：使用session对象创建一个producter对象
		MessageProducer producer = session.createProducer(queue);
		// 第七步：创建一个message对象，创建一个textmessage对象
		// 方法一：
		// TextMessage message = new ActiveMQTextMessage();
		// message.setText("hello activeMq, this is my first test.");
		// 方法二：
		TextMessage message = session.createTextMessage("hello activeMq, this is my first test.");
		// 第八步：使用producer对象发送消息
		producer.send(message);
		// 第九步：关闭资源
		producer.close();
		session.close();
		connection.close();
	}
}
