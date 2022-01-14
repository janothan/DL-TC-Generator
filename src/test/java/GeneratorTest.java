import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {


    @Test
    void getQueryResults(){
        final File resultDir = new File("./result-dir");
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
}