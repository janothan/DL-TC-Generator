package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Tc08SyntheticGeneratorTest {


    private static final String TC08_DIR = "./tc08-test-dir";

    @BeforeAll
    static void setUp(){
        tearDown();
    }

    @Test
    void testGenerator(){
        Tc08SyntheticGenerator generator = new Tc08SyntheticGenerator(TC08_DIR);
        generator.setNumberOfEdges(10);
        generator.setTotalNodesFactor(4);
        SyntheticGeneratorTest.testGenerator(generator, 10, 15);
    }

    @Test
    void getTcId() {
        Tc08SyntheticGenerator generator = new Tc08SyntheticGenerator(TC08_DIR);
        assertNotNull(generator.getTcId());
    }

    @Test
    void getSetTotalNodesFactor(){
        Tc08SyntheticGenerator generator = new Tc08SyntheticGenerator(TC08_DIR);
        generator.setTotalNodesFactor(10);
        assertEquals(10, generator.getTotalNodesFactor());
        generator.setTotalNodesFactor(1);
        assertEquals(10, generator.getTotalNodesFactor());
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(TC08_DIR);
    }
}