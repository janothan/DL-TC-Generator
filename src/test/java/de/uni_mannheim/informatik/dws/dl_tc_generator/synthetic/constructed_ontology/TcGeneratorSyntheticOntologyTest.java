package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.io.File;
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
    private static final String DIR_12 = "./synthetic_ontology_tc12";

    /**
     * A list of all synthetic generators which can be used in multiple test cases.
     */
    private static final List<TcGeneratorSyntheticOntology> generatorList = new ArrayList<>();

    static {
        Tc01GeneratorSyntheticOntology generator1 = new Tc01GeneratorSyntheticOntology(new File(DIR_1), 15, 20, 10, 5
                , 2, new int[]{10});

        // testing the constructor
        assertEquals(20, generator1.getNumberOfEdges());
        assertEquals(15, generator1.getNumberOfClasses());
        assertEquals(10, generator1.getTotalNodesFactor());
        assertEquals(5, generator1.getMaxTriplesPerNode());
        assertEquals(2, generator1.getBranchingFactor());

        generatorList.add(generator1);
        generatorList.add(new Tc02GeneratorSyntheticOntology(new File(DIR_2), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc03GeneratorSyntheticOntology(new File(DIR_3), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc04GeneratorSyntheticOntology(new File(DIR_4), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc05GeneratorSyntheticOntology(new File(DIR_5), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc06GeneratorSyntheticOntology(new File(DIR_6), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc07GeneratorSyntheticOntology(new File(DIR_7), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc08GeneratorSyntheticOntology(new File(DIR_8), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc09GeneratorSyntheticOntology(new File(DIR_9), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc10GeneratorSyntheticOntology(new File(DIR_10), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc11GeneratorSyntheticOntology(new File(DIR_11), 15, 20, 10, 5, 2, new int[]{10}));
        generatorList.add(new Tc12GeneratorSyntheticOntology(new File(DIR_12), 15, 20, 10, 5, 2, new int[]{10}));
    }

    /**
     * Testing some boundary conditions of all synthetic generators.
     */
    @Test
    void testAllSyntheticGenerators() {
        for (TcGeneratorSynthetic generator : generatorList) {
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
        Util.delete(DIR_12);
    }

}