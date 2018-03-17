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

    public static void main(String[] args) {
        final long start = System.nanoTime();
        Parameters.loadParameters("constellation.txt");

        eng = new Engine();
        screen = new GraphScreen(eng);
        eng.link(screen.getGraphConstellation());

        long simulationPlots = Parameters.SIMULATION_STEPS / Parameters.STEPS_PER_PLOT;
        for (long i = 0; i < simulationPlots; i++) {
            eng.run();
            screen.updateConstellation();
        }
        System.out.print(System.nanoTime() - start);
    }
}
