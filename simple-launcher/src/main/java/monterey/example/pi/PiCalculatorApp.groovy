package monterey.example.pi;

import monterey.brooklyn.MontereyConfig
import brooklyn.entity.basic.AbstractApplication
import brooklyn.entity.messaging.activemq.ActiveMQBroker
import brooklyn.launcher.BrooklynLauncher
import brooklyn.location.basic.LocalhostMachineProvisioningLocation

public class PiCalculatorApp extends AbstractApplication {
          
    public static void main(String[] argv) {
        // Create the app, configure it and have Brooklyn manage it
        PiCalculatorApp app = new PiCalculatorApp(displayName: "Pi calculator app")
        app.init()
        BrooklynLauncher.manage(app)
  
        // Start the app on localhost
        LocalhostMachineProvisioningLocation loc = new LocalhostMachineProvisioningLocation(count:10)
        app.start([loc])

    }
  
    public void init() {
        def config = new MontereyConfig()
        def monterey = config.network(this, displayName: "Pi Calculator Network",
                initialNumVenuesPerLocation:1, initialNumBrokersPerLocation:1) {
            brokers(ActiveMQBroker.class, jmxPort:11099)
            bundles {
                url "wrap:mvn:monterey-v4-examples/simple-actors/4.0.0-SNAPSHOT"
            }
            actors(defaultStrategy:"pojo") {
                type "monterey.example.pi.PiMaster"
                type "monterey.example.pi.PiCalculator"
                start "Pi Master!", type:"monterey.example.pi.PiMaster"
            }
        }
    }
}
