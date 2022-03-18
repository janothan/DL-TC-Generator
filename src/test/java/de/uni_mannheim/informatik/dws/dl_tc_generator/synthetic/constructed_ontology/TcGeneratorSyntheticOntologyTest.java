package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

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

class TcGeneratorSyntheticOntologyTest {


    @BeforeAll
    static void setUp(){
        cleanUp();
    }

    private static final String DIR_1 = "./synthetic_ontology_tc01";

    /**
     * A list of all synthetic generators which can be used in multiple test cases.
     */
    private static final List<Tc01GeneratorSyntheticOntology> generatorList = new ArrayList<>();

    static {
        generatorList.add(new Tc01GeneratorSyntheticOntology(DIR_1));
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
        Util.delete(DIR_1);
    }

}