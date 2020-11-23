package Tiger;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import burlap.debugtools.RandomFactory;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.observations.DiscreteObservationFunction;
import burlap.mdp.singleagent.pomdp.observations.ObservationProbability;
import burlap.mdp.singleagent.pomdp.observations.ObservationUtilities;

import java.util.ArrayList;
import java.util.List;

public class TigerObservations implements DiscreteObservationFunction {
    protected double listenAccuracy;
    protected boolean includeDoNothing;

    public TigerObservations(double listenAccuracy, boolean includeDoNothing) {
        this.listenAccuracy = listenAccuracy;
        this.includeDoNothing = includeDoNothing;
    }

    public List<State> allObservations() {
        List<State> result = new ArrayList(3);
        result.add(this.observationLeft());
        result.add(this.observationRight());
        result.add(this.observationReset());
        if (this.includeDoNothing) {
            result.add(this.observationNothing());
        }

        return result;
    }

    public State sample(State state, Action action) {
        if (!action.actionName().equals("openLeft") && !action.actionName().equals("openRight")) {
            if (action.actionName().equals("listen")) {
                String tigerVal = (String)state.get("behindDoor");
                double r = RandomFactory.getMapped(0).nextDouble();
                if (r < this.listenAccuracy) {
                    return tigerVal.equals("behindLeft") ? this.observationLeft() : this.observationRight();
                } else {
                    return tigerVal.equals("behindLeft") ? this.observationRight() : this.observationLeft();
                }
            } else if (action.actionName().equals("doNothing")) {
                return this.observationNothing();
            } else {
                throw new RuntimeException("Unknown action " + action.actionName() + "; cannot return observation sample.");
            }
        } else {
            return this.observationReset();
        }
    }

    public double probability(State observation, State state, Action action) {
        String oVal = (String)observation.get("observation");
        String tigerVal = (String)state.get("behindDoor");
        if (!action.actionName().equals("openLeft") && !action.actionName().equals("openRight")) {
            if (action.actionName().equals("listen")) {
                if (tigerVal.equals("behindLeft")) {
                    if (oVal.equals("hearLeft")) {
                        return this.listenAccuracy;
                    } else {
                        return oVal.equals("hearRight") ? 1.0D - this.listenAccuracy : 0.0D;
                    }
                } else if (oVal.equals("hearLeft")) {
                    return 1.0D - this.listenAccuracy;
                } else {
                    return oVal.equals("hearRight") ? this.listenAccuracy : 0.0D;
                }
            } else if (action.actionName().equals("doNothing")) {
                return oVal.equals("hearNothing") ? 1.0D : 0.0D;
            } else {
                throw new RuntimeException("Unknown action " + action.actionName() + "; cannot return observation probability.");
            }
        } else {
            return oVal.equals("reset") ? 1.0D : 0.0D;
        }
    }

    public List<ObservationProbability> probabilities(State state, Action action) {
        return ObservationUtilities.probabilitiesByEnumeration(this, state, action);
    }

    protected State observationLeft() {
        return new TigerObservation("hearLeft");
    }

    protected State observationRight() {
        return new TigerObservation("hearRight");
    }

    protected State observationReset() {
        return new TigerObservation("reset");
    }

    protected State observationNothing() {
        return new TigerObservation("hearNothing");
    }
}
