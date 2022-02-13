package de.uni_mannheim.informatik.dws.dl_tc_generator;

import de.uni_mannheim.informatik.dws.dl_tc_generator.by_query.GeneratorQuery;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;
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
        generateByQuery();
        //generateSynthetic();
    }


    private static void generateSynthetic() throws Exception {
        GeneratorSynthetic generator = new GeneratorSynthetic("synthetic_results");
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
