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
    private static final String DIR_2 = "./synthetic_ontology_tc02";
    private static final String DIR_3 = "./synthetic_ontology_tc03";
    private static final String DIR_4 = "./synthetic_ontology_tc04";
    private static final String DIR_5 = "./synthetic_ontology_tc05";
    private static final String DIR_6 = "./synthetic_ontology_tc06";
    private static final String DIR_7 = "./synthetic_ontology_tc07";
    private static final String DIR_8 = "./synthetic_ontology_tc08";
    private static final String DIR_9 = "./synthetic_ontology_tc09";
    private static final String DIR_10 = "./synthetic_ontology_tc10";
    private static final String DIR_11 = "./synthetic_ontology_tc11";

    /**
     * A list of all synthetic generators which can be used in multiple test cases.
     */
    private static final List<TcGeneratorSyntheticOntology> generatorList = new ArrayList<>();

    static {
        generatorList.add(new Tc01GeneratorSyntheticOntology(DIR_1));
        generatorList.add(new Tc02GeneratorSyntheticOntology(DIR_2));
        generatorList.add(new Tc03GeneratorSyntheticOntology(DIR_3));
        generatorList.add(new Tc04GeneratorSyntheticOntology(DIR_4));
        generatorList.add(new Tc05GeneratorSyntheticOntology(DIR_5));
        generatorList.add(new Tc06GeneratorSyntheticOntology(DIR_6));
        generatorList.add(new Tc07GeneratorSyntheticOntology(DIR_7));
        generatorList.add(new Tc08GeneratorSyntheticOntology(DIR_8));
        generatorList.add(new Tc09GeneratorSyntheticOntology(DIR_9));
        generatorList.add(new Tc10GeneratorSyntheticOntology(DIR_10));
        generatorList.add(new Tc11GeneratorSyntheticOntology(DIR_11));
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
        Util.delete(DIR_2);
        Util.delete(DIR_3);
        Util.delete(DIR_4);
        Util.delete(DIR_5);
        Util.delete(DIR_6);
        Util.delete(DIR_7);
        Util.delete(DIR_8);
        Util.delete(DIR_9);
        Util.delete(DIR_10);
        Util.delete(DIR_11);
    }

}