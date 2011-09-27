package prodder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import monterey.actor.ActorRef;
import monterey.venue.jms.spi.JmsAdmin;

import com.google.common.base.Throwables;

/**
 */
public class ActorProdder implements MessageListener {
    private final JmsAdmin admin;
    private final Connection connection;
    private final Session consumerSession;
    private final Session producerSession;
    private List<Message> messages = new ArrayList<Message>();

    public ActorProdder(JmsAdmin admin) throws JMSException {
        this.admin = admin;
        connection = admin.createConnection();
        connection.setClientID("prodder");
        consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        admin.createDurableSubscriptionToActorTopic(consumerSession, "prodder").setMessageListener(this);
        producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void close() throws JMSException {
        consumerSession.close();
        producerSession.close();
        connection.close();
    }

    public void sendTo(ActorRef ref, Serializable data) {
        try {
            Destination dest = admin.lookupActorTopic(producerSession, ref.getId());
            MessageProducer producer = producerSession.createProducer(dest);
            Message m = producerSession.createObjectMessage(data);
            m.setStringProperty(admin.getJmsSenderIdProperty(), "test-id");
            m.setStringProperty(admin.getJmsSenderDisplayNameProperty(), "test");
            m.setStringProperty(admin.getJmsSenderTypeProperty(), ActorProdder.class.getName());
            producer.send(m);
            producer.close();
        } catch (JMSException e) {
            Throwables.propagate(new RuntimeException(e));
        }
    }

    public void publish(String topic, Serializable data) {
        try {
            Destination dest = admin.lookupTopic(producerSession, topic);
            MessageProducer producer = producerSession.createProducer(dest);
            Message m = producerSession.createObjectMessage(data);
            m.setStringProperty(admin.getJmsSenderIdProperty(), "test-id");
            m.setStringProperty(admin.getJmsSenderDisplayNameProperty(), "test");
            m.setStringProperty(admin.getJmsSenderTypeProperty(), ActorProdder.class.getName());
            producer.send(m);
            producer.close();
        } catch (JMSException e) {
            Throwables.propagate(new RuntimeException(e));
        }
    }

    @Override
    public void onMessage(Message message) {
        messages.add(message);
    }
}
