/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Roberto
 */
public class ConstellationTest {

    static Constellation instance = new Constellation();
    static String constelationStr;

    public ConstellationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        constelationStr
                = "# Name, mass (Kg),  radio(m),       x0 (m), y0 (m), z0 (m), vx0 (m/s), vy0 (m/s), vz0 (m/s)\n"
                + "Sun,   1.98855e30, 695.700e6,            0,      0,      0,         0,         0,         0\n"
                + "Tierra, 5.9722e24,  6.3710e6, 149.598023e9,      0,      0,         0,  29.780e3,         0\n"
                + "#                            (149.598023e9 + 384.399e6)               (29.78e3 + 1.022e3)  \n"
                + "Luna,    7.349e22,  1.7371e6, 149.982422e9,   1100,      0,         0,  30.802e3,         0\n"
                + "Mars,   6.4171e23,  3.3895e6, 227.939200e9,      0,      0,         0,  24.007e3,         0\n";
        boolean result = instance.loadConstellation(constelationStr);
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
     * Test of loadConstellation method, of class Constellation.
     */
    @Test
    public void testLoadConstellation() {
        System.out.println("loadConstellation");
        boolean result = instance.loadConstellation(constelationStr);
        assertEquals(true, result);
    }

    /**
     * Test of calculateDistances method, of class Constellation.
     */
    @Test
    public void testCalculateDistances() {
        System.out.println("calculateDistances");
        instance.calculateDistances();
    }

    /**
     * Test of step_jerk method, of class Constellation.
     */
    @Test
    public void testStep_jerk() {
        System.out.println("step_jerk");
        double deltaTime = 0.0;
        instance.step_jerk(deltaTime);
    }

    /**
     * Test of step_basic method, of class Constellation.
     */
    @Test
    public void testStep_basic() {
        System.out.println("step_basic");
        double deltaTime = 0.0;
        instance.step_basic(deltaTime);
    }

    /**
     * Test of step_jerk_Schwarzschild method, of class Constellation.
     */
    @Test
    public void testStep_jerk_Schwarzschild() {
        System.out.println("step_jerk_Schwarzschild");
        double deltaTime = 0.0;
        instance.step_jerk_Schwarzschild(deltaTime);
    }

    /**
     * Test of step_basic_Schwarzschild method, of class Constellation.
     */
    @Test
    public void testStep_basic_Schwarzschild() {
        System.out.println("step_basic_Schwarzschild");
        double deltaTime = 0.0;
        instance.step_basic_Schwarzschild(deltaTime);
    }

    /**
     * Test of calculateGravity method, of class Constellation.
     */
    @Test
    public void testCalculateGravity() {
        System.out.println("calculateGravity");
        int i = 0;
        instance.calculateGravity(i);
    }

    /**
     * Test of calculateGravity_Schwarzschild method, of class Constellation.
     */
    @Test
    public void testCalculateGravity_Schwarzschild() {
        System.out.println("calculateGravity_Schwarzschild");
        int i = 0;
        instance.calculateGravity_Schwarzschild(i);
    }

    /**
     * Test of initGravity method, of class Constellation.
     */
    @Test
    public void testInitGravity() {
        System.out.println("initGravity");
        instance.initGravity();
    }

    /**
     * Test of pushToGraphic method, of class Constellation.
     */
    @Test
    public void testPushToGraphic() {
        System.out.println("pushToGraphic");
        Constellation instance = null;
        instance.pushToGraphic();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
