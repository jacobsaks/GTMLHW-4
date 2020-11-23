package LunarLander;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
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
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentQLearning;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
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
import burlap.domain.singleagent.lunarlander.LLVisualizer;
import burlap.domain.singleagent.lunarlander.LunarLanderDomain;
import burlap.domain.singleagent.lunarlander.state.LLAgent;
import burlap.domain.singleagent.lunarlander.state.LLBlock;
import burlap.domain.singleagent.lunarlander.state.LLState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BasicBehavior {

    LunarLanderDomain lunarLanderDomain;
    OOSADomain domain;
    TerminalFunction tf;
    StateConditionTest goalCondition;
    State initialState;
    HashableStateFactory hashingFactory;
    SimulatedEnvironment env;
    LLState state;
    TileCodingFeatures tilecoding;


    public BasicBehavior(){
        lunarLanderDomain = new LunarLanderDomain();
        lunarLanderDomain.setXmax(5.0);
        lunarLanderDomain.setYmax(5.0);
//        tf = new GridWorldTerminalFunction(29, 29);
//        lunarLanderDomain.setTf(tf);
//        goalCondition = new TFGoalCondition(tf);
        state = new LLState(new LLAgent(1, 0, 0), new LLBlock.LLPad(2, 5, 0, 2, "pad"));
        domain = lunarLanderDomain.generateDomain();

        ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures()
                .addObjectVectorizion(LunarLanderDomain.CLASS_AGENT, new NumericVariableFeatures());

        int nTilings = 5;
        double resolution = 10.;

        double xWidth = (lunarLanderDomain.getXmax() - lunarLanderDomain.getXmin()) / resolution;
        double yWidth = (lunarLanderDomain.getYmax() - lunarLanderDomain.getYmin()) / resolution;
        double velocityWidth = 2 * lunarLanderDomain.getVmax() / resolution;
        double angleWidth = 2 * lunarLanderDomain.getAngmax() / resolution;

        tilecoding = new TileCodingFeatures(inputFeatures);
        tilecoding.addTilingsForAllDimensionsWithWidths(
                new double[]{xWidth, yWidth, velocityWidth, velocityWidth, angleWidth},
                nTilings,
                TilingArrangement.RANDOM_JITTER);

        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, state);


        //VisualActionObserver observer = new VisualActionObserver(domain,
        //	GridWorldVisualizer.getVisualizer(gwdg.getMap()));
        //observer.initGUI();
        //env.addObservers(observer);
    }


    public void visualize(String outputpath){
        Visualizer v = LLVisualizer.getVisualizer(lunarLanderDomain.getPhysParams());
        new EpisodeSequenceVisualizer(v, domain, outputpath);
    }

    public void BFSExample(String outputPath){

        DeterministicPlanner planner = new BFS(domain, goalCondition, hashingFactory);
        Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "bfs");

    }

    public void DFSExample(String outputPath){

        DeterministicPlanner planner = new DFS(domain, goalCondition, hashingFactory);
        Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "dfs");

    }

    public void AStarExample(String outputPath){

        Heuristic mdistHeuristic = new Heuristic() {

            public double h(State s) {
                GridAgent a = ((GridWorldState)s).agent;
                double mdist = Math.abs(a.x-10) + Math.abs(a.y-10);

                return -mdist;
            }
        };

        DeterministicPlanner planner = new AStar(domain, goalCondition,
                hashingFactory, mdistHeuristic);
        Policy p = planner.planFromState(initialState);

        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "astar");

    }

    public void valueIterationExample(String outputPath){

        Planner planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 1000);
        Policy p = planner.planFromState(state);

        PolicyUtils.rollout(p, state, domain.getModel()).write(outputPath + "vi");

        simpleValueFunctionVis((ValueFunction)planner, p);
        //manualValueFunctionVis((ValueFunction)planner, p);

    }

    public void policyIterationExample(String outputPath){

        Planner planner = new PolicyIteration(domain, 0.99, hashingFactory, 0.001, 1000, 1000);
        Policy p = planner.planFromState(state);

        PolicyUtils.rollout(p, state, domain.getModel()).write(outputPath + "pi");

        simpleValueFunctionVis((ValueFunction)planner, p);
        //manualValueFunctionVis((ValueFunction)planner, p);

    }


    public void qLearningExample(String outputPath){

        double defaultQ = 0.5;
        DifferentiableStateActionValue vfa = tilecoding.generateVFA(defaultQ / 5);
        GradientDescentQLearning agent = new GradientDescentQLearning(domain, 0.99, vfa, 0.02);

        SimulatedEnvironment env = new SimulatedEnvironment(domain, state);
        long startTime   = System.nanoTime();
        //run learning for 50 episodes
        for(int i = 0; i < 1000; i++){
            Episode e = agent.runLearningEpisode(env);

            e.write(outputPath + "ql_" + i);
            System.out.println(i + ": " + e.maxTimeStep());

            //reset environment for next learning episode
            env.resetEnvironment();
        }
        long endTime   = System.nanoTime();
        double totalTime = (endTime - startTime)/1000000000.0;
        System.out.println("Runtime for VI: " + totalTime);

    }


    public void sarsaLearningExample(String outputPath){

        double defaultQ = 0.5;
        DifferentiableStateActionValue vfa = tilecoding.generateVFA(defaultQ / 5);
        GradientDescentSarsaLam agent = new GradientDescentSarsaLam(domain, 0.99, vfa, 0.02, 0.5);

        SimulatedEnvironment env = new SimulatedEnvironment(domain, state);
        for (int i = 0; i < 5000; i++) {
            Episode ea = agent.runLearningEpisode(env);
            ea.write(outputPath + "sarsa_" + i);
            System.out.println(i + ": " + ea.maxTimeStep());
            env.resetEnvironment();
        }

    }

    public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p){

        List<State> allStates = StateReachability.getReachableStates(
                initialState, domain, hashingFactory);
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
                allStates, 11, 11, valueFunction, p);
        gui.initGUI();

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


    public static void main(String[] args) {

        BasicBehavior example = new BasicBehavior();
        String outputPath = "output/";

        //example.BFSExample(outputPath);
        //example.DFSExample(outputPath);
        //example.AStarExample(outputPath);
        //example.valueIterationExample(outputPath);
        //example.policyIterationExample(outputPath);
        example.qLearningExample(outputPath);
        //example.sarsaLearningExample(outputPath);

        //example.experimentAndPlotter();

        example.visualize(outputPath);

    }

}