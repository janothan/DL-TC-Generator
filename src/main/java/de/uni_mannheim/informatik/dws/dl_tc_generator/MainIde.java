package de.uni_mannheim.informatik.dws.dl_tc_generator;

import de.uni_mannheim.informatik.dws.dl_tc_generator.by_query.GeneratorQuery;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.GeneratorSyntheticConstructed;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard.GeneratorSyntheticConstructedHard;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random.GeneratorSyntheticRandom;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * A class that is supposed to be used only in the IDE.
 */
public class MainIde {


    /**
     * IDE main class. Feel free to edit it in your IDE:
     * @param args No args.
     * @throws Exception Some exception.
     */
    public static void main(String[] args) throws Exception {
        //generateByQuery();
        //generateSyntheticRandom();
        //generateSyntheticConstructed();
        generateSyntheticConstructedHard();
    }

    private static void generateSyntheticConstructedHard() throws Exception {
        GeneratorSyntheticConstructedHard generator = new GeneratorSyntheticConstructedHard(
                "./results/synthetic_constructed_hard");
        generator.setSizes(new int[]{50, 500, 5000});
        generator.generateTestCases();
    }

    private static void generateSyntheticConstructed() throws Exception {
        GeneratorSyntheticConstructed generator = new GeneratorSyntheticConstructed("./results/synthetic_constructed");
        generator.setSizes(new int[]{50, 500, 5000});
        generator.generateTestCases();
    }

    private static void generateSyntheticRandom() throws Exception {
        GeneratorSynthetic generator = new GeneratorSyntheticRandom("synthetic_results");
        generator.generateTestCases();
    }

    private static void generateByQuery() throws Exception {
        final String resultDir = "./results/dbpedia";
        FileUtils.deleteDirectory(new File(resultDir));
        final String queriesPath = "/Users/janportisch/IdeaProjects/DL-TC-Generator/src/main/resources/queries";
        //String queriesPath = MainIde.class.getResource("queries").getPath();
        GeneratorQuery generator = new GeneratorQuery(queriesPath,resultDir);
        generator.setSizes(Defaults.SIZES);
        //generator.setIncludeOnlyCollection("tc1", "tc2");
        //generator.setIncludeOnlyTestCase("cities", "people");
        generator.setTimeoutInSeconds(6000);
        generator.generateTestCases();
        System.out.println("DONE");
    }
}
