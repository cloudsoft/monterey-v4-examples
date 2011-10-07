package monterey.example.helloworld;

import brooklyn.entity.basic.AbstractApplication
import brooklyn.launcher.BrooklynLauncher
import brooklyn.location.basic.LocalhostMachineProvisioningLocation

import monterey.brooklyn.MontereyConfig

public class HelloWorldApp extends AbstractApplication {

    public static void main(String[] argv) {
        // Create the app, configure it and have Brooklyn manage it
        HelloWorldApp app = new HelloWorldApp(displayName: "Hello, world! app")
        app.init()
        BrooklynLauncher.manage(app)

        // Start the app on localhost
        LocalhostMachineProvisioningLocation loc = new LocalhostMachineProvisioningLocation(count:10)
        app.start([loc])

    }

    public void init() {

        def config = new MontereyConfig()
        def monterey = config.network(this, name: "Hello, world! Network",
                initialNumVenuesPerLocation:1, initialNumBrokersPerLocation:1) {
            brokers("activemq", jmxPort:11099)
            bundles {
                url "wrap:file:///path/to/your/target/simple-actors-4.0.0-M1.jar"
            }
            actors(defaultStrategy:"pojo") {
                type "monterey.example.helloworld.HelloWorldActor"
            }
            venues {
                actor "monterey.example.helloworld.HelloWorldActor", displayName: "Greeta"
            }
        }
    }
}