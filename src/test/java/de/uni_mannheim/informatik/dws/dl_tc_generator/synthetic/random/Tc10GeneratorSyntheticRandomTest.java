package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Tc10GeneratorSyntheticRandomTest {


    private static final String TC10_DIR = "./tc10-test-dir";

    @BeforeAll
    static void setUp(){
        tearDown();
    }

    @Test
    void testGenerator(){
        Tc10GeneratorSyntheticRandom generator = new Tc10GeneratorSyntheticRandom(TC10_DIR);
        generator.setNumberOfEdges(10);
        generator.setTotalNodesFactor(4);
        TcGeneratorSyntheticRandomTest.testGenerator(generator, 10, 15);
    }

    @Test
    void getTcId() {
        Tc10GeneratorSyntheticRandom generator = new Tc10GeneratorSyntheticRandom(TC10_DIR);
        assertNotNull(generator.getTcId());
    }

    @Test
    void getSetTotalNodesFactor(){
        Tc10GeneratorSyntheticRandom generator = new Tc10GeneratorSyntheticRandom(TC10_DIR);
        generator.setTotalNodesFactor(10);
        assertEquals(10, generator.getTotalNodesFactor());
        generator.setTotalNodesFactor(1);
        assertEquals(10, generator.getTotalNodesFactor());
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(TC10_DIR);
    }
}