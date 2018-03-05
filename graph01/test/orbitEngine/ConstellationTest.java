/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

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
        String constelationStr = "tierra, 1000, 2000,1000,0,1000,1,1,0\n"
                + "luna, 100, 200,1100,1100,0,2,2,0 \n";

        Boolean it_was_ok = Constellation.loadConstellation(constelationStr);

        // TODO review the generated test code and remove the default call to fail.
        assertTrue("The test case is a prototype.", it_was_ok);

    }

}
