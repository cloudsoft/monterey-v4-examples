package simple;

import monterey.brooklyn.MontereyConfig;
import monterey.brooklyn.Venue;

import brooklyn.entity.messaging.activemq.ActiveMQBroker
import brooklyn.entity.Application;
import brooklyn.entity.basic.AbstractApplication;
import brooklyn.launcher.BrooklynLauncher;
import brooklyn.location.basic.LocalhostMachineProvisioningLocation;

import brooklyn.location.basic.jclouds.JcloudsLocationFactory;
import brooklyn.location.basic.jclouds.JcloudsLocation;

public class PiCalculatorApp extends AbstractApplication {
          
    public static void main(String[] argv) {
        // Create the app, configure it and have Brooklyn manage it
        PiCalculatorApp app = new PiCalculatorApp(displayName : "Pi calculator app")
        app.init()
        BrooklynLauncher.manage(app)
  
        // Start the app on localhost
        LocalhostMachineProvisioningLocation loc = new LocalhostMachineProvisioningLocation(count:10)
        app.start([loc])

        // Start the app on AWS-EC2
        // JcloudsLocationFactory locFactory = new JcloudsLocationFactory([
                // provider : "aws-ec2",
                // identity : "AKIAI36QSOSYFBBT5IYQ",
                // credential : "vK4U1IGVldAHfkXwdVCUt/68qX3tbJ+vgmL5k7fT",
                // sshPrivateKey : new File("/Users/sam/.ssh/id_rsa"),
                // sshPublicKey : new File("/Users/sam/.ssh/id_rsa.pub")
            // ])
     
        // JcloudsLocation loc = locFactory.newLocation("eu-west-1")
        // loc.setTagMapping([
            // (Venue.class.getName()):[
                // securityGroups:["brooklyn-all"]],
            // (ActiveMQBroker.class.getName()):[
                // securityGroups:["brooklyn-all"]]])
     
        // app.start([loc])
    }
  
    public void init() {
        def config = new MontereyConfig()
        def monterey = config.network(this, name:"my monterey network",
                initialNumVenuesPerLocation:1,
                initialNumBrokersPerLocation:1) {
            brokers("activemq", jmxPort:11099)
            bundles {
                url "wrap:file:///Users/sam/code/misc/monterey-pi-calculator/simple-actor/target/simple-actor-0.0.1-SNAPSHOT.jar"
            }
            actors(defaultStrategy:"pojo") {
                type "simple.PiMaster"
                type "simple.PiCalculator"
            }
            venues {
                actor "simple.PiMaster", displayName: "Pi Master!"
            }
        }
    }
}

