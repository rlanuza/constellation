/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterface;

import graphEngine.GraphConstellation;
import graphEngine.GraphScreen;
import orbitEngine.Engine;

/**
 *
 * @author Roberto Lanuza
 */
public class main {

    private static GraphScreen screen;
    private static GraphConstellation grConstellation;
    private static Engine eng;

    public static void main(String[] args) {
        final long start = System.nanoTime();
        grConstellation = new GraphConstellation();
        eng = new Engine(grConstellation, "constellation.txt");
        screen = new GraphScreen();
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 10; j++) {
                eng.run(1000);
            }
            screen.updateConstellation(grConstellation);
        }
        System.out.print(System.nanoTime() - start);
    }
}
