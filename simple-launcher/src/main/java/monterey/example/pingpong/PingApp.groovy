package monterey.example.pingpong;

import brooklyn.entity.basic.AbstractApplication
import brooklyn.launcher.BrooklynLauncher
import brooklyn.location.basic.LocalhostMachineProvisioningLocation

import monterey.brooklyn.MontereyConfig

public class PingApp extends AbstractApplication {

    public static void main(String[] argv) {
        // Create the app, configure it and have Brooklyn manage it
        PingApp app = new PingApp(displayName: "Ping-pong app")
        app.init()
        BrooklynLauncher.manage(app)

        // Start the app on localhost
        LocalhostMachineProvisioningLocation loc = new LocalhostMachineProvisioningLocation(count:10)
        app.start([loc])

    }

    public void init() {

        def config = new MontereyConfig()
        def monterey = config.network(this, name: "Ping-pong Network",
                initialNumVenuesPerLocation:1, initialNumBrokersPerLocation:1) {
            brokers("activemq", jmxPort:11099)
            bundles {
                url ""
            }
            actors(defaultStrategy:"pojo") {
                type PingActor.class.getName()
                type PongActor.class.getName()
            }
            venues {
                actor PingActor.class.getName(), displayName: "Ping actor"
            }
        }
    }
}
