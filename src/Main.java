import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException, FSAException {
        BufferedReader bf = new BufferedReader(new FileReader("fsa.txt"));
        ArrayList<String> s = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            s.add(bf.readLine());
        }

        try {
            FSA fsa = new FSA(s);
            if (fsa.complete) {
                System.out.println(Messages.complete.getValue());
            } else {
                System.out.println(Messages.incomplete.getValue());
            }

            if (!fsa.warnings.isEmpty()) {
                System.out.println("Warning:");
                for (String w : fsa.warnings) {
                    System.out.println(w);
                }
            }
        } catch (FSAException e) {
            System.out.println("Error:");
            System.out.println(e.getMessage());
        }


    }
}


class FSA {
    ArrayList<String> states = new ArrayList<>();
    ArrayList<String> alphabet = new ArrayList<>();
    ArrayList<String> initialStates = new ArrayList<>();
    ArrayList<String> finalStates = new ArrayList<>();
    ArrayList<String[]> transitions = new ArrayList<>();
    ArrayList<String> warnings = new ArrayList<>();
    HashMap<String, HashSet<String>> undirectedGraph = new HashMap<>(); // key : state, value : child states
    HashMap<String, HashMap<String, HashSet>> directedGraph = new HashMap<>(); // key : state, value : {key : transition, value : child states}
    boolean deterministic = true;
    boolean complete = true;

    FSA(ArrayList<String> data) throws FSAException {
        processData(data);

        allStatesReachable();
        isDisjoint();
        isDeterministic();
        isComplete();
    }

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


    private void processStates(String s) throws FSAException {
        if (!s.startsWith("states=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] states = s.substring(8, s.length() - 1).split(",");
        for (String state : states) {
            if (!FSA.isGoodStateName(state)) {
                throw new FSAException(Messages.E5.getValue());
            }
            this.states.add(state);
        }
    }

    private void processAlphas(String s) throws FSAException {
        if (!s.startsWith("alpha=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] alphas = s.substring(7, s.length() - 1).split(",");
        for (String alpha : alphas) {
            if (!FSA.isGoodAlphaName(alpha)) {
                throw new FSAException(Messages.E5.getValue());
            }
            this.alphabet.add(alpha);
        }
    }

    private void processInitState(String s) throws FSAException {
        if (!s.startsWith("init.st=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] initStates = s.substring(9, s.length() - 1).split(",");
        if (initStates.length < 1) {
            throw new FSAException(Messages.E4.getValue());
        }
        if (initStates.length > 1) {
            throw new FSAException(Messages.E5.getValue());
        }

        for (String state : initStates) {
            if (!this.states.contains(state)) {
                throw new FSAException(Messages.E1.getValue().formatted(state));
            }
            this.initialStates.add(state);
        }
    }

    private void processFinalState(String s) throws FSAException {
        if (!s.startsWith("fin.st=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] finalStates = s.substring(8, s.length() - 1).split(",");
        if (finalStates.length < 1) {
            this.warnings.add(Messages.W1.getValue());
        }
        for (String state : finalStates) {
            if (state.length() > 0) {
                if (!this.states.contains(state)) {
                    throw new FSAException(Messages.E1.getValue().formatted(state));
                }
                this.finalStates.add(state);
            }
        }
    }

    private void processTransitions(String s) throws FSAException {
        if (!s.startsWith("trans=[") || !s.endsWith("]")) {
            throw new FSAException(Messages.E5.getValue());
        }

        String[] transitions = s.substring(7, s.length() - 1).split(",");
        for (String trans : transitions) {
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

    public static boolean isGoodStateName(String s) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (char c : s.toCharArray()) {
            if (alphabet.indexOf(c) < 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean isGoodAlphaName(String s) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789_";

        for (char c : s.toCharArray()) {
            if (alphabet.indexOf(c) < 0) {
                return false;
            }
        }

        return true;
    }

    private void allStatesReachable() {
        ArrayList<String> visited = recursiveSearchInDirectedGraph(this.initialStates.get(0), new ArrayList<>());
        if (visited.size() < this.states.size()) {
            warnings.add(Messages.W2.getValue());
        }
    }

    private void isDisjoint() throws FSAException {
        ArrayList<String> visited = recursiveSearchInUndirectedGraph(this.initialStates.get(0), new ArrayList<>());
        if (visited.size() < this.states.size()) {
            throw new FSAException(Messages.E2.getValue());
        }
    }

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

    private boolean isComplete() {
        if (!isDeterministic()){
            this.complete = false;
        }
        for (HashMap<String, HashSet> transition : this.directedGraph.values()) {
            if (transition.keySet().size() < this.alphabet.size()){
                this.complete = false;
            }
        }
        return this.complete;
    }


    private ArrayList<String> recursiveSearchInUndirectedGraph(String start, ArrayList<String> visited) {
        for (String state : this.undirectedGraph.get(start)) {
            if (!visited.contains(state)) {
                visited.add(state);
                recursiveSearchInUndirectedGraph(state, visited);
            }
        }
        return visited;
    }

    private ArrayList<String> recursiveSearchInDirectedGraph(String start, ArrayList<String> visited) {
        for (HashMap<String, HashSet> transition : this.directedGraph.values()) {
            for (HashSet<String> states : transition.values()) {
                for (String state : states) {
                    if (!visited.contains(state)) {
                        visited.add(state);
                        recursiveSearchInDirectedGraph(state, visited);
                    }
                }
            }
        }
        return visited;

    }
}

class FSAException extends Exception {
    FSAException() {
        super();
    }

    FSAException(String s) {
        super(s);
    }

}

enum Messages {
    E1("E1: A state '%s' is not in the set of states"),
    E2("E2: Some states are disjoint"),
    E3("E3: A transition '%s' is not represented in the alphabet"),
    E4("E4: Initial state is not defined"),
    E5("E5: Input file is malformed"),
    W1("W1: Accepting state is not defined"),
    W2("W2: Some states are not reachable from the initial state"),
    W3("W3: FSA is nondeterministic"),
    complete("FSA is complete"),
    incomplete("FSA is incomplete");

    private String value;

    Messages(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}