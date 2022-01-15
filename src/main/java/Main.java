import org.apache.commons.io.FileUtils;

import java.io.File;

public class Main {


    public static void main(String[] args) throws Exception{
        final String resultDir = "./result";
        FileUtils.deleteDirectory(new File(resultDir));
        String queriesPath = Main.class.getResource("queries").getPath();
        Generator generator = new Generator(queriesPath,resultDir);
        generator.setSizes(new int[]{5, 10});
        generator.generateTestCases();
    }
}
