package example;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class DeployActors {
    private static JMXConnector jmxc;
    private static MBeanServerConnection mbsc;
    private static ObjectName name;

    public static void main(String...argv) throws Exception {
	    connect();
	    name = new ObjectName("monterey:type=Venue");
        Set<ObjectInstance> beans = mbsc.queryMBeans(name, null);
        
        createActor("pong", "example.pingpong.PongActor");
        createActor("ping", "example.pingpong.PingActor");
    }
    
    private static void createActor(String id, String type) throws Exception {
        mbsc.invoke(name, "createActor",
                new Object[] { id, "pojo", type, id, id, null },
                new String[] { String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName(), String.class.getName(), Map.class.getName() });
    }
 
    private static void connect() throws IOException {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-monterey");
        Hashtable env = new Hashtable();
        String[] creds = new String[] { "admin", "admin" };
        env.put(JMXConnector.CREDENTIALS, creds);
        jmxc = JMXConnectorFactory.connect(url, env);
        mbsc = jmxc.getMBeanServerConnection();
    }
}
