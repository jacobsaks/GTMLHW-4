package Tiger;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import burlap.mdp.auxiliary.StateGenerator;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.model.FullModel;
import burlap.mdp.singleagent.model.TransitionProb;

import java.util.Arrays;
import java.util.List;

public class TigerModel implements FullModel {
    public double correctDoor = 10.0D;
    public double wrongDoor = -100.0D;
    public double listen = -1.0D;
    public double nothing = 0.0D;
    protected StateGenerator sg = TigerDomain.randomSideStateGenerator(0.5D);

    public TigerModel(double correctDoor, double wrongDoor, double listen, double nothing) {
        this.correctDoor = correctDoor;
        this.wrongDoor = wrongDoor;
        this.listen = listen;
        this.nothing = nothing;
    }

    public List<TransitionProb> transitions(State s, Action a) {
        String aname = a.actionName();
        TigerState ts;
        double r;
        if (aname.equals("openLeft")) {
            ts = (TigerState)s;
            if(ts.door.equals("behindLeft")) {
                return Arrays.asList(new TransitionProb(1.0D, new EnvironmentOutcome(s, a, new TigerState("behindLeft"), this.wrongDoor, true)));
            } else {
                this.listen = this.listen * 2;
                return Arrays.asList(new TransitionProb(0.5D, new EnvironmentOutcome(s, a, new TigerState("behindLeft"), this.correctDoor, false)), new TransitionProb(0.5D, new EnvironmentOutcome(s, a, new TigerState("behindRight"), this.correctDoor, false)));
            }
        } else if (aname.equals("openRight")) {
            ts = (TigerState)s;
            if(ts.door.equals("behindRight")) {
                return Arrays.asList(new TransitionProb(1.0D, new EnvironmentOutcome(s, a, new TigerState("behindLeft"), this.wrongDoor, true)));
            } else {
                this.listen = this.listen * 2;
                return Arrays.asList(new TransitionProb(0.5D, new EnvironmentOutcome(s, a, new TigerState("behindLeft"), this.correctDoor, false)), new TransitionProb(0.5D, new EnvironmentOutcome(s, a, new TigerState("behindRight"), this.correctDoor, false)));
            }
        } else if (aname.equals("listen")) {
            return Arrays.asList(new TransitionProb(1.0D, new EnvironmentOutcome(s, a, s, this.listen, false)));
        } else if (aname.equals("doNothing")) {
            return Arrays.asList(new TransitionProb(1.0D, new EnvironmentOutcome(s, a, s, this.nothing, false)));
        } else {
            throw new RuntimeException("Unknown action " + a.toString());
        }
    }

    public EnvironmentOutcome sample(State s, Action a) {
        String aname = a.actionName();
        TigerState ts;
        if (aname.equals("openLeft")) {
            ts = (TigerState)s;
            if(ts.door.equals("behindLeft")) {
                return new EnvironmentOutcome(s, a, this.sg.generateState(), this.wrongDoor, true);
            } else {
                this.listen = this.listen * 2;
                return new EnvironmentOutcome(s, a, this.sg.generateState(), this.correctDoor, false);
            }
        } else if (aname.equals("openRight")) {
            ts = (TigerState)s;
            if(ts.door.equals("behindRight")) {
                return new EnvironmentOutcome(s, a, this.sg.generateState(), this.wrongDoor, true);
            } else {
                this.listen = this.listen * 2;
                return new EnvironmentOutcome(s, a, this.sg.generateState(), this.correctDoor, false);
            }
        } else if (aname.equals("listen")) {
            return new EnvironmentOutcome(s, a, s, this.listen, false);
        } else if (aname.equals("doNothing")) {
            return new EnvironmentOutcome(s, a, s, this.nothing, false);
        } else {
            throw new RuntimeException("Unknown action " + a.toString());
        }
    }

    public boolean terminal(State s) {
        return false;
    }
}

