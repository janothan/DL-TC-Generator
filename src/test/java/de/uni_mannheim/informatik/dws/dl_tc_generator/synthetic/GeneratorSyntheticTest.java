package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorSyntheticTest {


    private static final String DIR_1 = "./synthetic_tc";

    @Test
    void generateTestCases() {
        GeneratorSynthetic generator = new GeneratorSynthetic(DIR_1);
        generator.generateTestCases();
    }

    @AfterAll
    public static void cleanUp(){
        Util.delete(DIR_1);
    }
}