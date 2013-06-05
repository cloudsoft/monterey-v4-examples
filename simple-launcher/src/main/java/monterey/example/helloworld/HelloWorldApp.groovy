package monterey.example.helloworld;

import monterey.brooklyn.MontereyConfig
import brooklyn.entity.basic.AbstractApplication
import brooklyn.entity.basic.Entities
import brooklyn.entity.messaging.activemq.ActiveMQBroker
import brooklyn.entity.proxying.EntitySpecs
import brooklyn.launcher.BrooklynLauncher
import brooklyn.util.CommandLineUtil

import com.google.common.collect.Lists

public class HelloWorldApp extends AbstractApplication {

    @Override
    public void init() {
        def config = new MontereyConfig()
        def monterey = config.network(this, displayName: "Hello, world! Network") {
            brokers(ActiveMQBroker, jmxPort:11099, rmiPort:9001)
            bundles {
                url "wrap:mvn:monterey-v4-examples/simple-actors/4.0.0-M4" // MONTEREY_VERSION
            }
            actors(defaultStrategy:"pojo") {
                start "Greeta", type:"monterey.example.helloworld.HelloWorldActor"
            }
        }
    }
    
    public static void main(String[] argv) {
        List<String> args = Lists.newArrayList(argv);
        String port =  CommandLineUtil.getCommandLineOption(args, "--port", "8081+");
        String location = CommandLineUtil.getCommandLineOption(args, "--location", "localhost");

        BrooklynLauncher launcher = BrooklynLauncher.newInstance()
                .application(EntitySpecs.appSpec(HelloWorldApp.class).displayName("Hello World app"))
                .webconsolePort(port)
                .location(location)
                .start();
         
        Entities.dumpInfo(launcher.getApplications());
    }
}
