package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Tc09GeneratorSyntheticRandomTest {


    private static final String TC09_DIR = "./tc09-test-dir";

    @BeforeAll
    static void setUp(){
        tearDown();
    }

    @Test
    void testGenerator(){
        Tc09GeneratorSyntheticRandom generator = new Tc09GeneratorSyntheticRandom(TC09_DIR);
        generator.setNumberOfEdges(10);
        generator.setTotalNodesFactor(4);
        TcGeneratorSyntheticRandomTest.testGenerator(generator, 10, 15);
    }

    @Test
    void getTcId() {
        Tc09GeneratorSyntheticRandom generator = new Tc09GeneratorSyntheticRandom(TC09_DIR);
        assertNotNull(generator.getTcId());
    }

    @Test
    void getSetTotalNodesFactor(){
        Tc09GeneratorSyntheticRandom generator = new Tc09GeneratorSyntheticRandom(TC09_DIR);
        generator.setTotalNodesFactor(10);
        assertEquals(10, generator.getTotalNodesFactor());
        generator.setTotalNodesFactor(1);
        assertEquals(10, generator.getTotalNodesFactor());
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(TC09_DIR);
    }
}