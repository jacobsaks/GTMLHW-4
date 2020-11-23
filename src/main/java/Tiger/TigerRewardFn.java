//package Tiger;
//
//import burlap.mdp.core.action.Action;
//import burlap.mdp.core.state.State;
//import burlap.mdp.singleagent.model.RewardFunction;
//
//public class TigerRewardFn implements RewardFunction {
//
//    public TigerRewardFn(boolean openedADoor, boolean foundTiger){
//    }
//
//    @Override
//    public double reward(State s, Action a, State sprime) {
//
//        int ax = (Integer)s.get(VAR_X);
//        int ay = (Integer)s.get(VAR_Y);
//
//        //are they at goal location?
//        if(ax == this.goalX && ay == this.goalY){
//            return 100.;
//        }
//
//        return -1;
//    }
//
//
//}
