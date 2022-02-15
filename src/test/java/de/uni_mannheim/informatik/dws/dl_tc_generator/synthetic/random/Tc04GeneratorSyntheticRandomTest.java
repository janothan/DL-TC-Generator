package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class Tc04GeneratorSyntheticRandomTest {


    private static final String TC04_DIR = "./tc04-test-dir";

    @BeforeAll
    static void setUp(){
        tearDown();
    }

    @Test
    void testGenerator(){
        Tc04GeneratorSyntheticRandom generator = new Tc04GeneratorSyntheticRandom(TC04_DIR);
        TcGeneratorSyntheticRandomTest.testGenerator(generator);
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(TC04_DIR);
    }
}