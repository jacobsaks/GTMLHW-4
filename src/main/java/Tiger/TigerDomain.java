package Tiger;

import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.debugtools.RandomFactory;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.auxiliary.StateGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.UniformCostRF;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.SimulatedPOEnvironment;
import burlap.mdp.singleagent.pomdp.beliefstate.TabularBeliefState;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;
import burlap.shell.EnvironmentShell;
import burlap.statehashing.simple.SimpleHashableStateFactory;

public class TigerDomain implements DomainGenerator {
    public static final String VAR_DOOR = "behindDoor";
    public static final String VAR_HEAR = "observation";
    public static final String ACTION_LEFT = "openLeft";
    public static final String ACTION_RIGHT = "openRight";
    public static final String ACTION_LISTEN = "listen";
    public static final String ACTION_DO_NOTHING = "doNothing";
    public static final String VAL_LEFT = "behindLeft";
    public static final String VAL_RIGHT = "behindRight";
    public static final String HEAR_LEFT = "hearLeft";
    public static final String HEAR_RIGHT = "hearRight";
    public static final String DOOR_RESET = "reset";
    public static final String HEAR_NOTHING = "hearNothing";
    public static final String WAS_EATEN = "youWereEaten";
    protected boolean includeDoNothing = false;
    protected double listenAccuracy = 0.85D;
    public double correctDoorReward = 10.0D;
    public double wrongDoorReward = -100.0D;
    public double listenReward = -1.0D;
    public double nothingReward = 0.0D;

    public TigerDomain() {
    }

    public TigerDomain(boolean includeDoNothing) {
        this.includeDoNothing = includeDoNothing;
    }

    public TigerDomain(boolean includeDoNothing, double listenAccuracy) {
        this.includeDoNothing = includeDoNothing;
        this.listenAccuracy = listenAccuracy;
    }

    public boolean isIncludeDoNothing() {
        return this.includeDoNothing;
    }

    public void setIncludeDoNothing(boolean includeDoNothing) {
        this.includeDoNothing = includeDoNothing;
    }

    public double getListenAccuracy() {
        return this.listenAccuracy;
    }

    public void setListenAccuracy(double listenAccuracy) {
        this.listenAccuracy = listenAccuracy;
    }

    public double getCorrectDoorReward() {
        return this.correctDoorReward;
    }

    public void setCorrectDoorReward(double correctDoorReward) {
        this.correctDoorReward = correctDoorReward;
    }

    public double getWrongDoorReward() {
        return this.wrongDoorReward;
    }

    public void setWrongDoorReward(double wrongDoorReward) {
        this.wrongDoorReward = wrongDoorReward;
    }

    public double getListenReward() {
        return this.listenReward;
    }

    public void setListenReward(double listenReward) {
        this.listenReward = listenReward;
    }

    public double getNothingReward() {
        return this.nothingReward;
    }

    public void setNothingReward(double nothingReward) {
        this.nothingReward = nothingReward;
    }

    public Domain generateDomain() {
        PODomain domain = new PODomain();
        domain.addActionType(new UniversalActionType("openLeft")).addActionType(new UniversalActionType("openRight")).addActionType(new UniversalActionType("listen"));
        if (this.includeDoNothing) {
            domain.addActionType(new UniversalActionType("doNothing"));
        }

        ObservationFunction of = new TigerObservations(this.listenAccuracy, this.includeDoNothing);
        domain.setObservationFunction(of);
        TigerModel tigerModel = new TigerModel(this.correctDoorReward, this.wrongDoorReward, this.listenReward, this.nothingReward);
//        FactoredModel model = new FactoredModel(tigerModel, (RewardFunction)new UniformCostRF(), (TerminalFunction)tf);
//        domain.setModel(model);
        domain.setModel(tigerModel);
        StateEnumerator senum = new StateEnumerator(domain, new SimpleHashableStateFactory());
        senum.getEnumeratedID(new TigerState("behindLeft"));
        senum.getEnumeratedID(new TigerState("behindRight"));
        domain.setStateEnumerator(senum);
        return domain;
    }

    public static StateGenerator randomSideStateGenerator() {
        return randomSideStateGenerator(0.5D);
    }

    public static StateGenerator randomSideStateGenerator(final double probLeft) {
        return new StateGenerator() {
            public State generateState() {
                double roll = RandomFactory.getMapped(0).nextDouble();
                return roll < probLeft ? new TigerState("behindLeft") : new TigerState("behindRight");
            }
        };
    }

    public static TabularBeliefState getInitialBeliefState(PODomain domain) {
        TabularBeliefState bs = new TabularBeliefState(domain, domain.getStateEnumerator());
        bs.initializeBeliefsUniformly();
        return bs;
    }

    public static void main(String[] args) {
        TigerDomain dgen = new TigerDomain(false);
        PODomain domain = (PODomain)dgen.generateDomain();
        StateGenerator tigerGenerator = randomSideStateGenerator(0.5D);
        Environment observableEnv = new SimulatedEnvironment(domain, tigerGenerator);
        Environment poEnv = new SimulatedPOEnvironment(domain, tigerGenerator);
        Environment envTouse = poEnv;
        if (args.length > 0 && args[0].equals("h")) {
            envTouse = observableEnv;
        }

        EnvironmentShell shell = new EnvironmentShell(domain, (Environment)envTouse);
        shell.start();
    }
}

