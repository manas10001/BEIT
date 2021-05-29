
package pubsub;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author manas
 */
public class Publisher {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    
    public static void main(String args[])throws JMSException, InterruptedException{

        ConnectionFactory conFact = new ActiveMQConnectionFactory(url);
        Connection con = conFact.createConnection();
        con.start();
        
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        Topic topic = session.createTopic("PubSubMessage");
        
        MessageProducer producer = session.createProducer(topic);
        
        TextMessage message = session.createTextMessage();
        
        
        for(int i = 0; i < 100; i++) {
        	Thread.sleep(5000);
        	message.setText("Messgae "+i+" from manas");
	        producer.send(message);
	        System.out.println("Sent message " + message.getText());
	        
        }
        con.close();  
        
    }
    
}

