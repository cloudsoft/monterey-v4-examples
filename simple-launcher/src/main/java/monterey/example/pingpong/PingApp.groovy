package monterey.example.pingpong;

import monterey.brooklyn.MontereyConfig
import brooklyn.entity.basic.AbstractApplication
import brooklyn.entity.messaging.activemq.ActiveMQBroker
import brooklyn.launcher.BrooklynLauncher
import brooklyn.location.basic.LocalhostMachineProvisioningLocation

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
        def message = "test1"
        def config = new MontereyConfig()
        def monterey = config.network(this, displayName: "Ping-pong Network") {
            bundles {
                url "wrap:mvn:monterey-v4-examples/simple-actors/4.0.0-SNAPSHOT"
            }
            actors(defaultStrategy:"pojo") {
                type "monterey.example.pingpong.PingActor"
                type "monterey.example.pingpong.PongActor"
                start "Ping actor " + message, type:"monterey.example.pingpong.PingActor", config:["message": message]
            }
        }
    }

}
