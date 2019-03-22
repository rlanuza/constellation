package userInterface;

import graphEngine.GraphScreen;
import orbitEngine.Engine;

/**
 * Entry class to the constellation viewer and interplanetary rout calculator
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class main {

    /**
     * Graphic screen handler.
     */
    private static GraphScreen screen;
    /**
     * Orbital physics engine.
     */
    public static Engine eng;
    /**
     * Long argument string for parameters.
     */
    private static final String ARG_PARAM1 = "--parameters:";
    /**
     * Short argument string for parameters.
     */
    private static final String ARG_PARAM2 = "-p";
    /**
     * Long argument string for method.
     */
    private static final String ARG_METHOD1 = "--method:";
    /**
     * Short argument string for method.
     */
    private static final String ARG_METHOD2 = "-m";
    /**
     * Long argument string for command.
     */
    private static final String ARG_COMMAND1 = "--command:";
    /**
     * Short argument string for command.
     */
    private static final String ARG_COMMAND2 = "-c";
    /**
     * Long argument string for output file.
     */
    private static final String ARG_OUT1 = "--output:";
    /**
     * Short argument string for output file.
     */
    private static final String ARG_OUT2 = "-o";

    /**
     * Entry point
     *
     * @param args command line arguments.
     */
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
