package monterey.example.suspendresume;

import brooklyn.entity.basic.AbstractApplication
import brooklyn.launcher.BrooklynLauncher
import brooklyn.location.basic.LocalhostMachineProvisioningLocation

import monterey.brooklyn.MontereyConfig

public class SuspendResumeApp extends AbstractApplication {

    public static void main(String[] argv) {
        // Create the app, configure it and have Brooklyn manage it
        SuspendResumeApp app = new SuspendResumeApp(displayName: "Suspend/resume app")
        app.init()
        BrooklynLauncher.manage(app)

        // Start the app on localhost
        LocalhostMachineProvisioningLocation loc = new LocalhostMachineProvisioningLocation(count:10)
        app.start([loc])

    }

    public void init() {

        def config = new MontereyConfig()
        def monterey = config.network(this, name: "Suspend/resume Network",
                initialNumVenuesPerLocation:1, initialNumBrokersPerLocation:1) {
            brokers("activemq", jmxPort:11099)
            bundles {
                url "wrap:file:///path/to/your/target/simple-actors-4.0.0-M1.jar"
            }
            actors(defaultStrategy:"pojo") {
                type "monterey.example.suspendresume.SuspendResumeActor"
            }
            venues {
                actor "monterey.example.suspendresume.SuspendResumeActor", displayName: "Pi Master!"
            }
        }
    }
}

