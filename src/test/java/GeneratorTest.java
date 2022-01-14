import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorTest.class);

    @Test
    void generateTestCases(){
        final File resultDir = new File("./result-dir");
        resultDir.deleteOnExit();
        try {
            Generator generator = new Generator(loadFile("testQueries"), resultDir);
            generator.generateTestCases();
            assertTrue(true);
            File resultFile = new File(resultDir, "tc1/cities/50/positives.txt");
            assertTrue(resultFile.exists());
            String content = Generator.readUtf8(resultFile);
            assertTrue(content.contains("\n"));
            resultFile = new File(resultDir, "tc2/cities/500/positives.txt");
            assertTrue(resultFile.exists());
        } catch (Exception e){
            fail(e);
        }
    }

    @Test
    void getQueryResults(){
        final File resultDir = new File("./result-dir2");
        resultDir.deleteOnExit();
        try {
            Generator generator = new Generator(loadFile("testQueries"), resultDir);
            List<String> result = generator.getQueryResults(loadFile("testQuery.sparql"), 3);
            assertEquals(3, result.size());
        } catch (Exception e){
            fail(e);
        }
    }

    @Test
    void writeListToFile(){
        List<String> myList = new ArrayList<>();
        myList.add("Hello");
        myList.add("World!");
        File fileToWrite = new File("./testWriteFile.txt");
        fileToWrite.deleteOnExit();
        Generator.writeListToFile(fileToWrite, myList);
        String content = Generator.readUtf8(fileToWrite);
        assertTrue(content.contains("Hello\n"));
        assertTrue(content.contains("World!"));
    }

    @Test
    void readUtf8(){
        String result = Generator.readUtf8(loadFile("testfile.txt"));
        assertNotNull(result);
        assertTrue(result.contains("Hello World!\n"));
        assertTrue(result.contains("Visit Europe."));
        System.out.println(result);
    }

    /**
     * Helper function to load files in class path that contain spaces.
     * @param fileName Name of the file.
     * @return File in case of success, else null.
     */
    private File loadFile(String fileName){
        try {
            URL resultUri =  this.getClass().getClassLoader().getResource(fileName);
            assertNotNull(resultUri);
            File result = FileUtils.toFile(resultUri.toURI().toURL());
            assertTrue(result.exists(), "Required resource not available.");
            return result;
        } catch (URISyntaxException | MalformedURLException exception){
            exception.printStackTrace();
            fail("Could not load file.", exception);
            return null;
        }
    }

    @AfterAll
    static void cleanUp(){
        delete("./result-dir");
        delete("./result-dir2");
    }

    private static void delete(String filePath){
        File file = new File(filePath);
        if (!file.exists()){
            return;
        }
        if (file.isFile()){
            if(file.delete()){
                LOGGER.info("Delete file: '" + file.getName() + "'");
            }
        }
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            LOGGER.warn("An exception occurred while trying to remove directory: '" + file.getAbsolutePath() + "'");
        }

    }

}