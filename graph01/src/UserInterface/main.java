/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterface;

import graphEngine.GraphScreen;
import orbitEngine.Engine;

/**
 *
 * @author Roberto
 */
public class main {

    private static GraphScreen screen;
    private static Engine eng;

    public static void main(String[] args) {

        eng = new Engine("constellation.txt");
        
        screen = new GraphScreen();

    }

}