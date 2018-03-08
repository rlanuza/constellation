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
 * @author Roberto
 */
public class main {

    private static GraphScreen screen;
    private static GraphConstellation  gconstell;
    private static Engine eng;

    public static void main(String[] args) {
        gconstell = new GraphConstellation();
        eng = new Engine(gconstell,"constellation.txt");

        for (int i = 0; i < 10; i++) {
            eng.run();
            screen = new GraphScreen();
       }
    }
}
