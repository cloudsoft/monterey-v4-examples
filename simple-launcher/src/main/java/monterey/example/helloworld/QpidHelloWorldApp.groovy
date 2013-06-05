package monterey.example.helloworld;

import monterey.brooklyn.MontereyConfig
import monterey.brooklyn.qpid.QpidMontereyBroker
import monterey.venue.management.ActorMigrationMode
import brooklyn.entity.basic.AbstractApplication
import brooklyn.entity.basic.Entities
import brooklyn.entity.proxying.EntitySpecs
import brooklyn.launcher.BrooklynLauncher
import brooklyn.util.CommandLineUtil

import com.google.common.collect.Lists

public class QpidHelloWorldApp extends AbstractApplication {

    @Override
    public void init() {
        def config = new MontereyConfig()
        def monterey = config.network(this, displayName: "Qpid Hello, world! Network",
                actorMigrationMode:ActorMigrationMode.USE_BROKER_WITH_ATOMIC_SUBSCRIBER_SWITCH) {
            brokers(QpidMontereyBroker, amqpPort:5678, jmxPort:11099)
            bundles {
                url "wrap:mvn:monterey-v4-examples/simple-actors/4.0.0-SNAPSHOT" // MONTEREY_VERSION
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
                .application(EntitySpecs.appSpec(QpidHelloWorldApp.class).displayName("Qpid Hello World app"))
                .webconsolePort(port)
                .location(location)
                .start();
         
        Entities.dumpInfo(launcher.getApplications());
    }
}
