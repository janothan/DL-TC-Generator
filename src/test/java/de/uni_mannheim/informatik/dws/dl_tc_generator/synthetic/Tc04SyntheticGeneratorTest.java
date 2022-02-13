package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Tc04SyntheticGeneratorTest {


    private static final String TC04_DIR = "./tc04-test-dir";

    @BeforeAll
    static void setUp(){
        tearDown();
    }

    @Test
    void testGenerator(){
        Tc04SyntheticGenerator generator = new Tc04SyntheticGenerator(TC04_DIR);
        GeneratorSyntheticTest.testGenerator(generator);
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(TC04_DIR);
    }
}