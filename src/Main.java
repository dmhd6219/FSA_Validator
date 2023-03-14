import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, FSAException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<String> s = new ArrayList<>();

        for (int i = 0; i < 5; i++){
            s.add(bf.readLine());
        }

        FSA fsa = new FSA(s);


    }
}

class FSA{
    ArrayList<String> states = new ArrayList<>();
    ArrayList<String> alphabet = new ArrayList<>();
    ArrayList<String> initialStates = new ArrayList<>();
    ArrayList<String> finalStates = new ArrayList<>();
    ArrayList<String[]> transitions = new ArrayList<>();
    boolean deterministic = true;
    boolean complete = true;
    FSA(ArrayList<String> data) throws FSAException {
        processData(data);
    }

    private void processData(ArrayList<String> data) throws FSAException {
        processStates(data.get(0));
        processAlphas(data.get(1));
        processInitState(data.get(2));
        processFinalState(data.get(3));
        processTransitions(data.get(4));
    }


    private void processStates(String s) throws FSAException {
        if (!s.startsWith("states=[") || !s.endsWith("]")){
            throw new FSAException(Messages.E5.getValue());
        }
        for (String state : s.substring(8, s.length() - 1).split(",")){
            this.states.add(state);
        }
    }

    private void processAlphas(String s) throws FSAException {
        if (!s.startsWith("alpha=[") || !s.endsWith("]")){
            throw new FSAException(Messages.E5.getValue());
        }
        for (String alpha : s.substring(7, s.length() - 1).split(",")){
            this.alphabet.add(alpha);
        }
    }

    private void processInitState(String s) throws FSAException{
        if (!s.startsWith("init.st=[") || !s.endsWith("]")){
            throw new FSAException(Messages.E5.getValue());
        }
        for (String state : s.substring(9, s.length() - 1).split(",")){
            this.initialStates.add(state);
        }
    }

    private void processFinalState(String s) throws FSAException{
        if (!s.startsWith("fin.st=[") || !s.endsWith("]")){
            throw new FSAException(Messages.E5.getValue());
        }
        for (String state : s.substring(8, s.length() - 1).split(",")){
            this.finalStates.add(state);
        }
    }

    private void processTransitions(String s) throws FSAException{
        if (!s.startsWith("trans=[") || !s.endsWith("]")){
            throw new FSAException(Messages.E5.getValue());
        }
        for (String trans : s.substring(7, s.length() - 1).split(",")){
            String[] transition = trans.split(">");
            if (transition.length != 3){
                throw new FSAException(Messages.E5.getValue());
            }
            this.transitions.add(transition);
        }
    }
}

class FSAException extends Exception{
    FSAException(){
        super();
    }
    FSAException(String s){
        super(s);
    }

}

enum Messages{
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
    Messages (String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}