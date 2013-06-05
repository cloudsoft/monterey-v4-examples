package monterey.example.pingpong;

import monterey.brooklyn.MontereyConfig
import brooklyn.entity.basic.AbstractApplication
import brooklyn.entity.basic.Entities
import brooklyn.entity.messaging.activemq.ActiveMQBroker
import brooklyn.entity.proxying.EntitySpecs
import brooklyn.launcher.BrooklynLauncher
import brooklyn.util.CommandLineUtil

import com.google.common.collect.Lists

public class PingApp extends AbstractApplication {

    @Override
    public void init() {
        def message = "test1"
        def config = new MontereyConfig()
        def monterey = config.network(this, displayName: "Ping-pong Network") {
            brokers(ActiveMQBroker, jmxPort:11099, rmiPort:9001)
            bundles {
                url "wrap:mvn:monterey-v4-examples/simple-actors/4.0.0-SNAPSHOT" // MONTEREY_VERSION
            }
            actors(defaultStrategy:"pojo") {
                type "monterey.example.pingpong.PingActor"
                type "monterey.example.pingpong.PongActor"
                start "Ping actor " + message, type:"monterey.example.pingpong.PingActor", config:["message": message]
            }
        }
    }

    public static void main(String[] argv) {
        List<String> args = Lists.newArrayList(argv);
        String port =  CommandLineUtil.getCommandLineOption(args, "--port", "8081+");
        String location = CommandLineUtil.getCommandLineOption(args, "--location", "localhost");

        BrooklynLauncher launcher = BrooklynLauncher.newInstance()
                .application(EntitySpecs.appSpec(PingApp.class).displayName("Ping-pong app"))
                .webconsolePort(port)
                .location(location)
                .start();
         
        Entities.dumpInfo(launcher.getApplications());
    }
}
