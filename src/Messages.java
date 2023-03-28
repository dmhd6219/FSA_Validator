/**
 * Messages for FSA.
 */
public enum Messages {
    /**
     * The Error #1.
     */
    E1("E1: A state '%s' is not in the set of states"),
    /**
     * The Error #2.
     */
    E2("E2: Some states are disjoint"),
    /**
     * The Error #3.
     */
    E3("E3: A transition '%s' is not represented in the alphabet"),
    /**
     * The Error #4.
     */
    E4("E4: Initial state is not defined"),
    /**
     * The Error #5.
     */
    E5("E5: Input file is malformed"),
    /**
     * The Warning #1.
     */
    W1("W1: Accepting state is not defined"),
    /**
     * The Warning #2.
     */
    W2("W2: Some states are not reachable from the initial state"),
    /**
     * The Warning #3.
     */
    W3("W3: FSA is nondeterministic"),
    /**
     * Complete message.
     */
    complete("FSA is complete"),
    /**
     * Incomplete message.
     */
    incomplete("FSA is incomplete");

    private String value;

    Messages(String value) {
        this.value = value;
    }

    /**
     * Returns message.
     *
     * @return the string
     */
    public String getValue() {
        return this.value;
    }
}
