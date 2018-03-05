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

    public ConstellationTest() {
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
     * Test of loadConstellation method, of class Constellation.
     */
    @Test
    public void testLoadConstellation() {
        System.out.println("loadConstellation");
        String constelationStr
                = "# Name, mass (Kg),  radio(m),       x0 (m), y0 (m), z0 (m), vx0 (m/s), vy0 (m/s), vz0 (m/s)\n"
                + "Sun,   1.98855e30, 695.700e6,            0,      0,      0,         0,         0,         0\n"
                + "Tierra, 5.9722e24,  6.3710e6, 149.598023e9,      0,      0,         0,  29.780e3,         0\n"
                + "#                            (149.598023e9 + 384.399e6)               (29.78e3 + 1.022e3)  \n"
                + "Luna,    7.349e22,  1.7371e6, 149.982422e9,   1100,      0,         0,  30.802e3,         0\n"
                + "Mars,   6.4171e23,  3.3895e6, 227.939200e9,      0,      0,         0,  24.007e3,         0\n";

        Boolean it_was_ok = Constellation.loadConstellation(constelationStr);

        // TODO review the generated test code and remove the default call to fail.
        assertTrue("The test case is a prototype.", it_was_ok);

    }

}
