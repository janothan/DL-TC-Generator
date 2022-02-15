package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Tc05GeneratorSyntheticRandomTest {


    private static final String TC05_DIR = "./tc05-test-dir";

    @BeforeAll
    static void setUp(){
        tearDown();
    }

    @Test
    void testGenerator(){
        Tc05GeneratorSyntheticRandom generator = new Tc05GeneratorSyntheticRandom(TC05_DIR);
        TcGeneratorSyntheticRandomTest.testGenerator(generator);
    }

    @Test
    void getTcId() {
        Tc05GeneratorSyntheticRandom generator = new Tc05GeneratorSyntheticRandom(TC05_DIR);
        assertNotNull(generator.getTcId());
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(TC05_DIR);
    }
}