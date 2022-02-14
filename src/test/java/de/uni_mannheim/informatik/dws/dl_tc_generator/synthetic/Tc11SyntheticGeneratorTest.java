package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Tc11SyntheticGeneratorTest {

    private static final String TC11_DIR = "./tc11-test-dir";

    @BeforeAll
    static void setUp(){
        tearDown();
    }

    @Test
    void testGenerator(){
        Tc11SyntheticGenerator generator = new Tc11SyntheticGenerator(TC11_DIR);
        generator.setNumberOfEdges(10);
        generator.setTotalNodesFactor(4);
        SyntheticGeneratorTest.testGenerator(generator, 10, 15);
    }

    @Test
    void getTcId() {
        Tc11SyntheticGenerator generator = new Tc11SyntheticGenerator(TC11_DIR);
        assertNotNull(generator.getTcId());
    }

    @Test
    void getSetTotalNodesFactor(){
        Tc11SyntheticGenerator generator = new Tc11SyntheticGenerator(TC11_DIR);
        generator.setTotalNodesFactor(10);
        assertEquals(10, generator.getTotalNodesFactor());
        generator.setTotalNodesFactor(1);
        assertEquals(10, generator.getTotalNodesFactor());
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(TC11_DIR);
    }

}