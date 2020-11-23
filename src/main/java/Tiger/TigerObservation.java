package Tiger;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import burlap.mdp.core.state.MutableState;

import java.util.Arrays;
import java.util.List;

public class TigerObservation implements MutableState {
    public String hear;

    public TigerObservation() {
        this.hear = "hearNothing";
    }

    public TigerObservation(String hear) {
        if (!hear.equals("hearLeft") && !hear.equals("hearRight") && !hear.equals("hearNothing") && !hear.equals("reset")) {
            throw new RuntimeException("Value must be either hearLeft, hearRight, hearNothing, or reset");
        } else {
            this.hear = hear;
        }
    }

    public MutableState set(Object variableKey, Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException("Value must be a String");
        } else {
            String hear = (String)value;
            if (!hear.equals("hearLeft") && !hear.equals("hearRight") && !hear.equals("hearNothing") && !hear.equals("reset")) {
                throw new RuntimeException("Value must be either hearLeft, hearRight, hearNothing, or reset");
            } else {
                this.hear = hear;
                return this;
            }
        }
    }

    public List<Object> variableKeys() {
        return Arrays.asList((Object)"observation");
    }

    public Object get(Object variableKey) {
        return this.hear;
    }

    public burlap.domain.singleagent.pomdp.tiger.TigerObservation copy() {
        return new burlap.domain.singleagent.pomdp.tiger.TigerObservation(this.hear);
    }

    public String toString() {
        return this.hear;
    }
}
