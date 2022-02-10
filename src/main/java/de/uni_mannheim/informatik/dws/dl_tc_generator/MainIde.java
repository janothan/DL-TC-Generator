package de.uni_mannheim.informatik.dws.dl_tc_generator;

import de.uni_mannheim.informatik.dws.dl_tc_generator.by_query.GeneratorQuery;
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

        final String resultDir = "./result_for_testing";
        FileUtils.deleteDirectory(new File(resultDir));
        String queriesPath = MainIde.class.getResource("queries").getPath();
        GeneratorQuery generator = new GeneratorQuery(queriesPath,resultDir);
        generator.setSizes(new int[]{25, 50});
        generator.setIncludeOnlyCollection("tc1", "tc2");
        generator.setIncludeOnlyTestCase("cities", "people");
        generator.setTimeoutInSeconds(6000);
        generator.generateTestCases();
        System.out.println("DONE");
    }
}
