package pubsub;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
/**
 *
 * @author manas
 */
public class Subscriber {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    
    public static void main(String args[]) throws JMSException, InterruptedException{
        ConnectionFactory conFact = new ActiveMQConnectionFactory(url);
        Connection con = conFact.createConnection();
        con.start();
        
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        Topic topic = session.createTopic("PubSubMessage");
        
        MessageConsumer consumer = session.createConsumer(topic);
        for(int i = 0; i < 100; i++) {
		    MessageListener listener = new MessageListener(){
		        public void onMessage(Message message){
		            try{
		                if(message instanceof TextMessage){
		                    TextMessage textMessage = (TextMessage) message;
		                    System.out.println("Message Recieved: "+textMessage.getText());
		                }else{
		                    System.out.println("No Message Recieved: ");
		                }
		            }catch(Exception e){
		                System.out.println("Exception in subscriber: "+e.toString());
		            }
		        }
		    };
		    consumer.setMessageListener(listener);
		    Thread.sleep(500);
        }
        con.close();
    }
}

