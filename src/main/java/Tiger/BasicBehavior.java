package Tiger;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.deterministic.DeterministicPlanner;
import burlap.behavior.singleagent.planning.deterministic.informed.Heuristic;
import burlap.behavior.singleagent.planning.deterministic.informed.astar.AStar;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.behavior.singleagent.planning.deterministic.uninformed.dfs.DFS;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.domain.singleagent.pomdp.tiger.TigerDomain;
import burlap.domain.singleagent.pomdp.tiger.TigerState;
import burlap.mdp.auxiliary.StateGenerator;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;

import java.awt.*;
import java.util.List;

public class BasicBehavior {

    static TigerDomain dgen;
    PODomain domain;
    TerminalFunction tf;
    StateConditionTest goalCondition;
    State initialState;
    HashableStateFactory hashingFactory;
    SimulatedEnvironment env;


    public BasicBehavior() {
        dgen = new TigerDomain(false);
        dgen.setCorrectDoorReward(10.0);
        domain = (PODomain) dgen.generateDomain();

        initialState = new TigerState();
        StateGenerator tigerGenerator = TigerDomain.randomSideStateGenerator(0.5);
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, tigerGenerator);
    }


    public void BFSExample(String outputPath){

        DeterministicPlanner planner = new BFS(domain, goalCondition, hashingFactory);
        Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "bfs");

    }

    public void valueIterationExample(String outputPath){

        Planner planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 100);
        Policy p = planner.planFromState(initialState);

        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "vi");

        simpleValueFunctionVis((ValueFunction)planner, p);
        //manualValueFunctionVis((ValueFunction)planner, p);

    }

    public void policyIterationExample(String outputPath){

        Planner planner = new PolicyIteration(domain, 0.99, hashingFactory, 0.001, 100, 100);
        Policy p = planner.planFromState(initialState);

        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "pi");

        simpleValueFunctionVis((ValueFunction)planner, p);
        //manualValueFunctionVis((ValueFunction)planner, p);

    }


    public void qLearningExample(String outputPath){

        LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0., 1.);

        //run learning for 50 episodes
        for(int i = 0; i < 1; i++){
            Episode e = agent.runLearningEpisode(env);

            e.write(outputPath + "ql_" + i);
            System.out.println(i + ": " + e.maxTimeStep());

            //reset environment for next learning episode
            env.resetEnvironment();
        }

    }


    public void sarsaLearningExample(String outputPath){

        LearningAgent agent = new SarsaLam(domain, 0.99, hashingFactory, 0., 0.5, 0.3);

        //run learning for 50 episodes
        for(int i = 0; i < 50; i++){
            Episode e = agent.runLearningEpisode(env);

            e.write(outputPath + "sarsa_" + i);
            System.out.println(i + ": " + e.maxTimeStep());

            //reset environment for next learning episode
            env.resetEnvironment();
        }

    }

    public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p){

        List<State> allStates = StateReachability.getReachableStates(
                initialState, domain, hashingFactory);

    }

    public void manualValueFunctionVis(ValueFunction valueFunction, Policy p){

        List<State> allStates = StateReachability.getReachableStates(
                initialState, domain, hashingFactory);

        //define color function
        LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
        rb.addNextLandMark(0., Color.RED);
        rb.addNextLandMark(1., Color.BLUE);

        //define a 2D painter of state values,
        //specifying which attributes correspond to the x and y coordinates of the canvas
        StateValuePainter2D svp = new StateValuePainter2D(rb);
        svp.setXYKeys("agent:x", "agent:y",
                new VariableDomain(0, 11), new VariableDomain(0, 11),
                1, 1);

        //create our ValueFunctionVisualizer that paints for all states
        //using the ValueFunction source and the state value painter we defined
        ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(
                allStates, svp, valueFunction);

        //define a policy painter that uses arrow glyphs for each of the grid world actions
        PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
        spp.setXYKeys("agent:x", "agent:y", new VariableDomain(0, 11),
                new VariableDomain(0, 11),
                1, 1);

        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_NORTH, new ArrowActionGlyph(0));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_SOUTH, new ArrowActionGlyph(1));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_EAST, new ArrowActionGlyph(2));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_WEST, new ArrowActionGlyph(3));
        spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);


        //add our policy renderer to it
        gui.setSpp(spp);
        gui.setPolicy(p);

        //set the background color for places where states are not rendered to grey
        gui.setBgColor(Color.GRAY);

        //start it
        gui.initGUI();



    }


    public void experimentAndPlotter(){

        //different reward function for more structured performance plots
        ((FactoredModel)domain.getModel()).setRf(new GoalBasedRF(this.goalCondition, 5.0, -0.1));

        /**
         * Create factories for Q-learning agent and SARSA agent to compare
         */
        LearningAgentFactory qLearningFactory = new LearningAgentFactory() {

            public String getAgentName() {
                return "Q-Learning";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.1);
            }
        };

        LearningAgentFactory sarsaLearningFactory = new LearningAgentFactory() {

            public String getAgentName() {
                return "SARSA";
            }


            public LearningAgent generateAgent() {
                return new SarsaLam(domain, 0.99, hashingFactory, 0.0, 0.1, 1.);
            }
        };

        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(
                env, 10, 100, qLearningFactory, sarsaLearningFactory);
        exp.setUpPlottingConfiguration(500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
                PerformanceMetric.AVERAGE_EPISODE_REWARD);

        exp.startExperiment();
        exp.writeStepAndEpisodeDataToCSV("expData");

    }

//    public void visualize(String outputpath){
//        new EpisodeSequenceVisualizer(null, domain, outputpath);
//    }


    public static void main(String[] args) {

        BasicBehavior example = new BasicBehavior();
        String outputPath = "output/";

        System.out.println("listen: " + dgen.listenReward);
        System.out.println("wrong: " + dgen.wrongDoorReward);
        System.out.println("correct: " + dgen.correctDoorReward);

        //example.BFSExample(outputPath);
        //example.DFSExample(outputPath);
        //example.AStarExample(outputPath);
        example.valueIterationExample(outputPath);
        //example.policyIterationExample(outputPath);
        //example.qLearningExample(outputPath);
        //example.sarsaLearningExample(outputPath);

        //example.experimentAndPlotter();

    }

}