package userInterface;

import graphEngine.GraphScreen;
import orbitEngine.Engine;

/**
 *
 * @author Roberto Lanuza
 */
public class main {

    private static GraphScreen screen;
    public static Engine eng;
    private static final String ARG_PARAM1 = "--parameters:";
    private static final String ARG_PARAM2 = "-p";
    private static final String ARG_METHOD1 = "--method:";
    private static final String ARG_METHOD2 = "-m";
    private static final String ARG_COMMAND1 = "--command:";
    private static final String ARG_COMMAND2 = "-c";
    private static final String ARG_OUT1 = "--output:";
    private static final String ARG_OUT2 = "-o";

    public static void main(String[] args) {
        String parametersFile = null;
        String calculusMethod = null;
        String commandFile = null;
        String outputFile = null;
        for (String s : args) {
            if (s.startsWith(ARG_PARAM1)) {
                parametersFile = s.substring(ARG_PARAM1.length());
            } else if (s.startsWith(ARG_PARAM2)) {
                parametersFile = s.substring(ARG_PARAM2.length());
            } else if (s.startsWith(ARG_METHOD1)) {
                calculusMethod = s.substring(ARG_METHOD1.length());
            } else if (s.startsWith(ARG_METHOD2)) {
                calculusMethod = s.substring(ARG_METHOD2.length());
            } else if (s.startsWith(ARG_COMMAND1)) {
                commandFile = s.substring(ARG_COMMAND1.length());
            } else if (s.startsWith(ARG_COMMAND2)) {
                commandFile = s.substring(ARG_COMMAND2.length());
            } else if (s.startsWith(ARG_OUT1)) {
                outputFile = s.substring(ARG_OUT1.length());
            } else if (s.startsWith(ARG_OUT2)) {
                outputFile = s.substring(ARG_OUT2.length());
            }
        }
        if (parametersFile == null) {
            System.out.print("Options:\n"
                    + "\t" + ARG_PARAM2 + "<parameter_file>, " + ARG_PARAM1 + "<parameter_file>\n"
                    + "\t[" + ARG_METHOD2 + "calculus_method, " + ARG_METHOD1 + "calculus_method]\n"
                    + "\t[" + ARG_COMMAND2 + "<command_file>, " + ARG_COMMAND1 + "<command_file>]\n"
                    + "\t[" + ARG_OUT2 + "<output_file>, " + ARG_OUT1 + "<output_file>]\n"
                    + "\t-h, --help\n");
        } else {
            final long start = System.nanoTime();
            new Parameter(parametersFile, calculusMethod);

            eng = new Engine();
            screen = new GraphScreen(eng);

            if (commandFile != null) {
                eng.runSimulationTravel(new Command(commandFile), new Report(outputFile));
            } else {
                eng.runSimulation();
            }
            System.out.printf("Elapsed time %f\n", (System.nanoTime() - start) / 1.0e9);
        }
    }

}
