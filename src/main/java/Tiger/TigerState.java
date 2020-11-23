package Tiger;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.annotations.DeepCopyState;

import java.util.Arrays;
import java.util.List;

@DeepCopyState
public class TigerState implements MutableState {
    public String door;

    public TigerState() {
        this.door = "behindLeft";
    }

    public TigerState(String door) {
        if (!door.equals("behindLeft") && !door.equals("behindRight")) {
            throw new RuntimeException("Value must be either behindLeft or behindRight");
        } else {
            this.door = door;
        }
    }

    @Override
    public MutableState set(Object variableKey, Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException("Value must be a String");
        } else {
            String val = (String)value;
            if (!val.equals("behindLeft") && !val.equals("behindRight")) {
                throw new RuntimeException("Value must be either behindLeft or behindRight");
            } else {
                this.door = val;
                return this;
            }
        }
    }

    @Override
    public List<Object> variableKeys() {
        return Arrays.asList((Object)"behindDoor");
    }

    @Override
    public Object get(Object variableKey) {
        return this.door;
    }

    @Override
    public TigerState copy() {
        return new TigerState(this.door);
    }

    public String toString() {
        return this.door;
    }

    public int hashCode() {
        return this.door.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof TigerState)) {
            return false;
        } else {
            TigerState o = (TigerState)obj;
            return this.door.equals(o.door);
        }
    }
}

