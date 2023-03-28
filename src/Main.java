import java.io.*;
import java.util.*;

/**
 * The Main class.
 */
public class Main {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException  the io exception
     */
    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader("fsa.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("result.txt"));
        ArrayList<String> s = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            s.add(bf.readLine());
        }

        try {
            FSA fsa = new FSA(s);
            if (fsa.complete) {
                bw.write(Messages.complete.getValue());
                bw.write("\n");
            } else {
                bw.write(Messages.incomplete.getValue());
                bw.write("\n");
            }

            if (!fsa.warnings.isEmpty()) {
                bw.write("Warning:\n");
                for (String w : fsa.warnings) {
                    bw.write(w);
                    bw.write("\n");
                }
            }
        } catch (FSAException e) {
            bw.write("Error:\n");
            bw.write(e.getMessage());
            bw.write("\n");
        }

        bw.close();

    }
}

