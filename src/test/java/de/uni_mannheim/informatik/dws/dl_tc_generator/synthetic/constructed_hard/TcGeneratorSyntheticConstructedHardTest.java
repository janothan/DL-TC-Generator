package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.TcGeneratorSyntheticConstructedTest.testGenerator;

import static org.junit.jupiter.api.Assertions.*;

class TcGeneratorSyntheticConstructedHardTest {


    private static final String DIR_6 = "./synthetic_constructed_hard_tc06h";
    private static final String DIR_7 = "./synthetic_constructed_hard_tc07h";

    /**
     * A list of all synthetic generators which can be used in multiple test cases.
     */
    private static final List<TcGeneratorSynthetic> generatorList = new ArrayList<>();

    static {
        generatorList.add(new Tc06GeneratorSyntheticConstructedHard(DIR_6));
        generatorList.add(new Tc07GeneratorSyntheticConstructedHard(DIR_7));
    }

    @BeforeAll
    static void setUp(){
        cleanUp();
    }

    /**
     * Testing some boundary conditions of all synthetic generators.
     */
    @Test
    void testAllSyntheticGenerators() {
        for (TcGeneratorSynthetic generator : generatorList) {
            generator.setNumberOfEdges(10);
            testGenerator(generator, 100, 200);
        }
    }

    @Test
    void testUniqueName(){
        Set<String> ids = new HashSet<>();
        generatorList.forEach(x -> assertNotNull(x.getTcId()));
        generatorList.forEach(x -> ids.add(x.getTcId()));
        assertEquals(ids.size(), generatorList.size());
    }

    @AfterAll
    public static void cleanUp() {
        Util.delete(DIR_6);
        Util.delete(DIR_7);
    }

}