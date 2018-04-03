/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private static final String ARG_COMMAND1 = "--command:";
    private static final String ARG_COMMAND2 = "-c";
    private static final String ARG_OUT1 = "--output:";
    private static final String ARG_OUT2 = "-o";

    public static void main(String[] args) {
        String parametersFile = null;
        String commandFile = null;
        String outputFile = null;
        for (String s : args) {
            if (s.startsWith(ARG_PARAM1)) {
                parametersFile = s.substring(ARG_PARAM1.length());
            } else if (s.startsWith(ARG_PARAM2)) {
                parametersFile = s.substring(ARG_PARAM2.length());
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
                    + "\t[" + ARG_COMMAND2 + "<command_file>, " + ARG_COMMAND1 + "<command_file>]\n"
                    + "\t[" + ARG_OUT2 + "<output_file>, " + ARG_OUT1 + "<output_file>]\n"
                    + "\t-h, --help\n");
        } else {
            final long start = System.nanoTime();
            Parameter.loadParameters(parametersFile);

            eng = new Engine();
            screen = new GraphScreen(eng);
            eng.link(screen.getGraphConstellation());

            if (commandFile != null) {
                Command.loadCommand(commandFile);
                runSimulationTravel();
            } else {
                runSimulation();
            }

            System.out.printf("Elapsed time %f\n", (System.nanoTime() - start) / 1.0e9);
        }
    }

    private static void runSimulationTravel() {
        long simulationPlots = Parameter.SIMULATION_STEPS / Parameter.STEPS_PER_PLOT;
        for (long i = 0; i < simulationPlots; i++) {
            eng.run(Parameter.STEPS_PER_PLOT);
            screen.updateConstellation();
        }
    }

    private static void runSimulation() {
        long simulationPlots = Parameter.SIMULATION_STEPS / Parameter.STEPS_PER_PLOT;
        for (long i = 0; i < simulationPlots; i++) {
            eng.run(Parameter.STEPS_PER_PLOT);
            screen.updateConstellation();
        }
    }

}
