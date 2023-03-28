import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class that represents Finite State Machine.
 */
public class FSA {
    /**
     * Array of states.
     */
    ArrayList<String> states = new ArrayList<>();
    /**
     * Array with alphabet.
     */
    ArrayList<String> alphabet = new ArrayList<>();
    /**
     * Array with Initial states.
     */
    ArrayList<String> initialStates = new ArrayList<>();
    /**
     * Array with Final states.
     */
    ArrayList<String> finalStates = new ArrayList<>();
    /**
     * Array with Transitions.
     */
    ArrayList<String[]> transitions = new ArrayList<>();
    /**
     * The Warnings.
     */
    HashSet<String> warnings = new HashSet<>();
    /**
     * The Undirected graph.
     * <p>
     * key : state, value : child states
     */
    HashMap<String, HashSet<String>> undirectedGraph = new HashMap<>();
    /**
     * The Directed graph.
     * <p>
     * key : state, value : {key : transition, value : child states}
     */
    HashMap<String, HashMap<String, HashSet>> directedGraph = new HashMap<>();
    /**
     * Contains {@code true} if this {@link FSA} is deterministic, otherwise {@code false}.
     */
    boolean deterministic = true;
    /**
     * Contains {@code true} if this {@link FSA} is complete, otherwise {@code false}.
     */
    boolean complete = true;

    /**
     * Instantiates a new FSA.
     *
     * @param data input strings
     * @throws FSAException the FSA Exception
     */
    FSA(ArrayList<String> data) throws FSAException {
        processData(data);

        allStatesReachable();
        isDisjoint();
        isDeterministic();
        isComplete();
    }

    /**
     * Handles all input strings.
     *
     * @param data input strings
     * @throws FSAException the FSA Exception
     */
    private void processData(ArrayList<String> data) throws FSAException {
        processStates(data.get(0));
        processAlphas(data.get(1));
        processInitState(data.get(2));
        processFinalState(data.get(3));
        processTransitions(data.get(4));

        for (String state : this.states) {
            this.undirectedGraph.put(state, new HashSet<>());
            this.directedGraph.put(state, new HashMap<>());
            for (String a : this.alphabet) {
                this.directedGraph.get(state).put(a, new HashSet<>());
            }
        }

        for (String[] transition : this.transitions) {
            this.undirectedGraph.get(transition[0]).add(transition[2]);
            this.undirectedGraph.get(transition[2]).add(transition[0]);

            this.directedGraph.get(transition[0]).get(transition[1]).add(transition[2]);
        }


    }

    /**
     * Handles input string with {@code states}.
     *
     * @param s input strings
     * @throws FSAException the FSA Exception
     */
    private void processStates(String s) throws FSAException {
        if (!s.startsWith("states=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] states = s.substring(8, s.length() - 1).split(",");
        for (String state : states) {
            if (state.length() == 0) {
                continue;
            }

            if (!FSA.isGoodStateName(state)) {
                throw new FSAException(Messages.E5.getValue());
            }
            this.states.add(state);
        }
    }

    /**
     * Handles input string with {@code alphabet}.
     *
     * @param s input strings
     * @throws FSAException the FSA Exception
     */
    private void processAlphas(String s) throws FSAException {
        if (!s.startsWith("alpha=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] alphas = s.substring(7, s.length() - 1).split(",");
        for (String alpha : alphas) {
            if (alpha.length() == 0) {
                continue;
            }

            if (!FSA.isGoodAlphaName(alpha)) {
                throw new FSAException(Messages.E5.getValue());
            }
            this.alphabet.add(alpha);
        }
    }

    /**
     * Handles input string with {@code initialStates}.
     *
     * @param s input strings
     * @throws FSAException the FSA Exception
     */
    private void processInitState(String s) throws FSAException {
        if (!s.startsWith("init.st=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] initStates = s.substring(9, s.length() - 1).split(",");

        for (String state : initStates) {
            if (state.length() == 0) {
                continue;
            }
            if (!this.states.contains(state)) {
                throw new FSAException(Messages.E1.getValue().formatted(state));
            }
            this.initialStates.add(state);
        }

        if (this.initialStates.size() < 1) {
            throw new FSAException(Messages.E4.getValue());
        }
        if (this.initialStates.size() > 1) {
            throw new FSAException(Messages.E5.getValue());
        }
    }

    /**
     * Handles input string with {@code finalStates}.
     *
     * @param s input strings
     * @throws FSAException the FSA Exception
     */
    private void processFinalState(String s) throws FSAException {
        if (!s.startsWith("fin.st=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] finalStates = s.substring(8, s.length() - 1).split(",");
        for (String state : finalStates) {
            if (state.length() == 0) {
                continue;
            }
            if (!this.states.contains(state)) {
                throw new FSAException(Messages.E1.getValue().formatted(state));
            }
            this.finalStates.add(state);
        }

        if (this.finalStates.size() < 1) {
            this.warnings.add(Messages.W1.getValue());
        }
    }

    /**
     * Handles input string with {@code transitions}.
     *
     * @param s input strings
     * @throws FSAException the FSA Exception
     */
    private void processTransitions(String s) throws FSAException {
        if (!s.startsWith("trans=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] transitions = s.substring(7, s.length() - 1).split(",");
        for (String trans : transitions) {
            if (trans.length() == 0) {
                continue;
            }
            String[] transition = trans.split(">");

            if (!this.states.contains(transition[0])) {
                throw new FSAException(Messages.E1.getValue().formatted(transition[0]));
            }
            if (!this.alphabet.contains(transition[1])) {
                throw new FSAException(Messages.E3.getValue().formatted(transition[1]));
            }
            if (!this.states.contains(transition[2])) {
                throw new FSAException(Messages.E1.getValue().formatted(transition[2]));
            }

            if (transition.length != 3) {
                throw new FSAException(Messages.E5.getValue());
            }
            this.transitions.add(transition);
        }
    }

    /**
     * Checks if name for {@code State} is good.
     *
     * @param s name
     * @return {@code true} if name is good, otherwise {@code false}
     */
    public static boolean isGoodStateName(String s) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (char c : s.toCharArray()) {
            if (alphabet.indexOf(c) < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if name for {@code alphabet} is good.
     *
     * @param s name
     * @return {@code true} if name is good, otherwise {@code false}
     */
    public static boolean isGoodAlphaName(String s) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789_";

        for (char c : s.toCharArray()) {
            if (alphabet.indexOf(c) < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if all states of this {@link FSA} are reachable from initial state.
     */
    private void allStatesReachable() {
        ArrayList<String> visited = recursiveSearchInDirectedGraph(this.initialStates.get(0), new ArrayList<>());
        if (visited.size() < this.states.size()) {
            warnings.add(Messages.W2.getValue());
        }
    }

    /**
     * Checks if this {@link FSA} has some disjoint states.
     */
    private void isDisjoint() throws FSAException {
        ArrayList<String> visited = recursiveSearchInUndirectedGraph(this.initialStates.get(0), new ArrayList<>());
        if (visited.size() < this.states.size()) {
            throw new FSAException(Messages.E2.getValue());
        }
    }

    /**
     * Checks if this {@link FSA} is deterministic.
     *
     * @return {@code true} if this {@link FSA} is deterministic, otherwise {@code false}
     */
    private boolean isDeterministic() {
        for (HashMap<String, HashSet> transition : this.directedGraph.values()) {
            for (HashSet<String> states : transition.values()) {
                if (states.size() > 1) {
                    this.deterministic = false;
                    this.warnings.add(Messages.W3.getValue());
                }
            }
        }
        return this.deterministic;
    }

    /**
     * Checks if this {@link FSA} is complete.
     *
     * @return {@code true} if this {@link FSA} is complete, otherwise {@code false}
     */
    private boolean isComplete() {
        if (!isDeterministic()) {
            this.complete = false;
        }
        for (HashMap<String, HashSet> transition : this.directedGraph.values()) {
            for (HashSet<String> states : transition.values()) {
                if (states.size() == 0) {
                    this.complete = false;
                }
            }
        }
        return this.complete;
    }


    /**
     * Checks which states are joint.
     *
     * @return array with visited states
     */
    private ArrayList<String> recursiveSearchInUndirectedGraph(String start, ArrayList<String> visited) {
        for (String state : this.undirectedGraph.get(start)) {
            if (!visited.contains(state)) {
                visited.add(state);
                recursiveSearchInUndirectedGraph(state, visited);
            }
        }
        return visited;
    }

    /**
     * Checks which states are reachable from initial state.
     *
     * @return array with visited states
     */
    private ArrayList<String> recursiveSearchInDirectedGraph(String start, ArrayList<String> visited) {
        for (HashSet<String> states : this.directedGraph.get(start).values()) {
            for (String state : states) {
                if (!visited.contains(state)) {
                    visited.add(state);
                    recursiveSearchInDirectedGraph(state, visited);
                }
            }
        }
        return visited;

    }
}
