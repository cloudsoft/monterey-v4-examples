package monterey.example.pi;

import brooklyn.entity.basic.AbstractApplication
import brooklyn.launcher.BrooklynLauncher
import brooklyn.location.basic.LocalhostMachineProvisioningLocation

import monterey.brooklyn.MontereyConfig

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
        def monterey = config.network(this, name: "Pi Calculator Network",
                initialNumVenuesPerLocation:1, initialNumBrokersPerLocation:1) {
            brokers("activemq", jmxPort:11099)
            bundles {
                url ""
            }
            actors(defaultStrategy:"pojo") {
                type PiMaster.class.getName()
                type PiCalculator.class.getName()
            }
            venues {
                actor PiMaster.class.getName(), displayName: "Pi Master!"
            }
        }
    }
}

