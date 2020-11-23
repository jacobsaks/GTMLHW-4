package Tiger;

import burlap.mdp.auxiliary.StateGenerator;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.SimulatedPOEnvironment;
import burlap.shell.EnvironmentShell;
import burlap.visualizer.Visualizer;

public class Tiger {

    public static void main(String[] args) {
        TigerDomain dgen = new TigerDomain(false);
        TigerState tigerState = new TigerState();
        PODomain domain = (PODomain) dgen.generateDomain();
        StateGenerator tigerGenerator = TigerDomain.randomSideStateGenerator(0.5);
        Environment observableEnv = new SimulatedEnvironment(domain, tigerGenerator);
        Environment poEnv = new SimulatedPOEnvironment(domain, tigerGenerator);
        Environment envTouse = poEnv;
        if (args.length > 0 && args[0].equals("h")) {
            envTouse = observableEnv;
        }
        EnvironmentShell shell = new EnvironmentShell(domain, envTouse);
        shell.start();


    }
}