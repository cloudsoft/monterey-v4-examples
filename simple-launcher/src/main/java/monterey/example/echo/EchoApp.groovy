package monterey.example.echo;

import java.util.List;

import monterey.brooklyn.MontereyConfig;
import brooklyn.entity.basic.AbstractApplication;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.proxying.EntitySpecs;
import brooklyn.launcher.BrooklynLauncher;
import brooklyn.util.CommandLineUtil;

import com.google.common.collect.Lists;

public class EchoApp extends AbstractApplication {

    @Override
    public void init() {
        def config = new MontereyConfig()
        def monterey = config.network(this, displayName: "Echo Network") {
            bundles {
                url "wrap:mvn:monterey-v4-examples/simple-actors/4.0.0-SNAPSHOT"
            }
            actors(defaultStrategy:"pojo") {
                start "Echoer", type:"monterey.example.echo.EchoActor"
            }
        }
    }
    
    public static void main(String[] argv) {
        List<String> args = Lists.newArrayList(argv);
        String port =  CommandLineUtil.getCommandLineOption(args, "--port", "8081+");
        String location = CommandLineUtil.getCommandLineOption(args, "--location", "localhost");

        BrooklynLauncher launcher = BrooklynLauncher.newInstance()
                .application(EntitySpecs.appSpec(EchoApp.class).displayName("Echo app"))
                .webconsolePort(port)
                .location(location)
                .start();
         
        Entities.dumpInfo(launcher.getApplications());
    }
}
