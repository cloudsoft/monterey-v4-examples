package monterey.example.migratable;

import monterey.brooklyn.MontereyConfig
import brooklyn.entity.basic.AbstractApplication
import brooklyn.entity.basic.Entities
import brooklyn.entity.messaging.activemq.ActiveMQBroker
import brooklyn.entity.proxying.EntitySpecs
import brooklyn.launcher.BrooklynLauncher
import brooklyn.util.CommandLineUtil

import com.google.common.collect.Lists

public class MigratableApp extends AbstractApplication {

    @Override
    public void init() {
        def config = new MontereyConfig()
        def monterey = config.network(this, displayName: "MigratableApp Test Network",
                initialNumVenuesPerLocation:1, initialNumBrokersPerLocation:1) {
            brokers(ActiveMQBroker.class, jmxPort:9999)
            bundles {
                url "wrap:mvn:monterey-v4-examples/simple-actors/4.0.0-SNAPSHOT" // MONTEREY_VERSION
            }
            actors(defaultStrategy:"pojo") {
                type "monterey.example.migratable.MigratableActor"
                start "Migrata", type: "monterey.example.migratable.MigratableActor"
            }
        }
    }
    
    public static void main(String[] argv) {
        List<String> args = Lists.newArrayList(argv);
        String port =  CommandLineUtil.getCommandLineOption(args, "--port", "8081+");
        String location = CommandLineUtil.getCommandLineOption(args, "--location", "localhost");

        BrooklynLauncher launcher = BrooklynLauncher.newInstance()
                .application(EntitySpecs.appSpec(MigratableApp.class).displayName("Migratable app"))
                .webconsolePort(port)
                .location(location)
                .start();
         
        Entities.dumpInfo(launcher.getApplications());
    }
}
