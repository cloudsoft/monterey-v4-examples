package monterey.example.migratable;

import monterey.brooklyn.MontereyConfig
import brooklyn.entity.basic.AbstractApplication
import brooklyn.entity.messaging.activemq.ActiveMQBroker
import brooklyn.launcher.BrooklynLauncher
import brooklyn.location.basic.LocalhostMachineProvisioningLocation

public class MigratableApp extends AbstractApplication {

    public static void main(String[] argv) {
        // Create the app, configure it and have Brooklyn manage it
        MigratableApp app = new MigratableApp(displayName: "Migratable app")
        app.init()
        BrooklynLauncher.manage(app)

        // Start the app on localhost
        
        LocalhostMachineProvisioningLocation loc = new LocalhostMachineProvisioningLocation(count:10)
        app.start([loc])

    }

    public void init() {

        def config = new MontereyConfig()
        def monterey = config.network(this, displayName: "MigratableApp Test Network",
                initialNumVenuesPerLocation:1, initialNumBrokersPerLocation:1) {
            brokers(ActiveMQBroker.class, jmxPort:9999)
            bundles {
                url "wrap:mvn:monterey-v4-examples/simple-actors/4.0.0-SNAPSHOT"
            }
            actors(defaultStrategy:"pojo") {
                type "monterey.example.migratable.MigratableActor"
                start "Migrata", type: "monterey.example.migratable.MigratableActor"
            }
        }
    }
}

