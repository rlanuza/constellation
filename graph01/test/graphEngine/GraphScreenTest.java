/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphEngine;

import java.awt.Color;
import java.awt.Graphics;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Roberto
 */
public class GraphScreenTest {
    
    public GraphScreenTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addLine method, of class GraphScreen.
     */
    @Test
    public void testAddLine_4args() {
        System.out.println("addLine");
        double x1 = 0.0;
        double x2 = 0.0;
        double x3 = 0.0;
        double x4 = 0.0;
        GraphScreen instance = new GraphScreen();
        instance.addLine(x1, x2, x3, x4);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addLine method, of class GraphScreen.
     */
    @Test
    public void testAddLine_5args() {
        System.out.println("addLine");
        double x1 = 0.0;
        double x2 = 0.0;
        double x3 = 0.0;
        double x4 = 0.0;
        Color color = null;
        GraphScreen instance = new GraphScreen();
        instance.addLine(x1, x2, x3, x4, color);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearLines method, of class GraphScreen.
     */
    @Test
    public void testClearLines() {
        System.out.println("clearLines");
        GraphScreen instance = new GraphScreen();
        instance.clearLines();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of zoomIn method, of class GraphScreen.
     */
    @Test
    public void testZoomIn_0args() {
        System.out.println("zoomIn");
        GraphScreen instance = new GraphScreen();
        instance.zoomIn();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of zoomOut method, of class GraphScreen.
     */
    @Test
    public void testZoomOut_0args() {
        System.out.println("zoomOut");
        GraphScreen instance = new GraphScreen();
        instance.zoomOut();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of zoomIn method, of class GraphScreen.
     */
    @Test
    public void testZoomIn_int_int() {
        System.out.println("zoomIn");
        int zcX = 0;
        int zcY = 0;
        GraphScreen instance = new GraphScreen();
        instance.zoomIn(zcX, zcY);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of zoomOut method, of class GraphScreen.
     */
    @Test
    public void testZoomOut_int_int() {
        System.out.println("zoomOut");
        int zcX = 0;
        int zcY = 0;
        GraphScreen instance = new GraphScreen();
        instance.zoomOut(zcX, zcY);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of paintComponent method, of class GraphScreen.
     */
    @Test
    public void testPaintComponent() {
        System.out.println("paintComponent");
        Graphics g = null;
        GraphScreen instance = new GraphScreen();
        instance.paintComponent(g);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
