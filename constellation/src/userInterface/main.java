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

        eng = new Engine("constellation.txt");
        screen = new GraphScreen(eng);
        eng.link(screen.getGraphConstellation());

        long simulationSteps = Parameters.SIMULATION_STEPS;
        for (long i = 0; i < simulationSteps; i++) {
            eng.run();
            screen.updateConstellation();
        }
        System.out.print(System.nanoTime() - start);
    }
}
