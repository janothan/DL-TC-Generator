import org.apache.commons.io.FileUtils;

import java.io.File;

public class Main {


    public static void main(String[] args) throws Exception {
        final String resultDir = "./resultTC1";
        FileUtils.deleteDirectory(new File(resultDir));
        String queriesPath = Main.class.getResource("queries").getPath();
        Generator generator = new Generator(queriesPath,resultDir);
        //generator.setSizes(new int[]{50, 500, 5000});
        generator.setSizes(new int[]{500});
        generator.setIncludeOnlyCollection("tc1");
        generator.setIncludeOnlyTestCase("cities");
        generator.setTimeoutInSeconds(180);
        generator.generateTestCases();
        System.out.println("DONE");
    }
}
