package testActiveMQ;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestQueueConsumer {
	@Test
	public void testQueueConsumer() throws Exception {
		// 第一步：创建一个ConnectionFactory对象
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 第二步：从ConnectionFactory中获取一个connection对象
		Connection connection = connectionFactory.createConnection();
		// 第三步：开启connection
		connection.start();
		// 第四步：使用connection对象创建一个session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 第五步：使用session对象创建一个destination对象，和发送端保持一致queue，并且队列的名称要一致
		Queue queue = session.createQueue("test-queue");
		// 第六步：使用session对象创建一个consumer对象
		MessageConsumer consumer = session.createConsumer(queue);
		// 第七步：接受消息
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				TextMessage testMessage = (TextMessage) message;
				String text=null;
				try {
					// 第八步：获取消息内容
					text = testMessage.getText();
					// 第九步：打印消息
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		//系统阻塞
		System.out.println("系统等待接受消息。。。。。。。");
//		System.in.read();
		System.out.println("系统关闭。。。。。。。。");
		// 第十步：关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
}
