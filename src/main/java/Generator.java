import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdfconnection.RDFConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Generator {


    private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);
    private static final String DATASET_URL = "http://dws-04.informatik.uni-mannheim.de:3030/dbpedia-all-2021-09";

    private static final String POSITIVE_FILE_NAME = "positive_query.sparql";
    private static final String NEGATIVE_FILE_NAME = "negative_query.sparql";
    private static final String NEGATIVE_HARD_FILE_NAME = "negative_query_hard.sparql";

    private File queryDirectory;

    /**
     * The generated directory must not exist yet.
     */
    private File generatedDirectory;
    private RDFConnection connection;
    private int[] sizes = {50, 500, 5000};

    public Generator(String queryDirectoryPath, String directoryToGeneratePath) {
        this(new File(queryDirectoryPath), new File(directoryToGeneratePath));
    }

    public Generator(File queryDirectory, File directoryToGenerate){
        this.queryDirectory = queryDirectory;
        this.generatedDirectory = directoryToGenerate;

        if(!sanityChecks()){
            System.exit(1);
        }

        generatedDirectory.mkdirs();
        connection = RDFConnection.connect(DATASET_URL);

        for(File tcFile : queryDirectory.listFiles()){
            if(!tcFile.isDirectory()){
                LOGGER.info("Skipping file: " + tcFile.getAbsolutePath());
            }
            File[] tcFiles = tcFile.listFiles();
            if(tcFiles == null || tcFiles.length == 0){
                LOGGER.info("Skipping empty directory: " + tcFile.getAbsolutePath());
                continue;
            }

            Set<String> fileNames = Arrays.stream(tcFiles).map(File::getName).collect(Collectors.toSet());
            if(!fileNames.contains(POSITIVE_FILE_NAME)){
                LOGGER.info("Directory (" + tcFile.getAbsolutePath() + ") is missing file '" + POSITIVE_FILE_NAME +
                        "'. Skipping directory.");
                continue;
            }
            if(!fileNames.contains(NEGATIVE_FILE_NAME)){
                LOGGER.info("Directory (" + tcFile.getAbsolutePath() + ") is missing file '" + NEGATIVE_FILE_NAME +
                        "'. Skipping directory.");
                continue;
            }

            // for writing later
            /*
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite), StandardCharsets.UTF_8))) {

            }

             */

        }

    }

    public List<String> getQueryResults(File file, int size){
        String query = readUtf8(file);
        query = query.replace("<number>", "" + size);
        List<String> result = new ArrayList<>();

        QueryExecution qe = connection.query(query);
        ResultSet rs = qe.execSelect();

        while(rs.hasNext()){
            QuerySolution qs = rs.next();
            result.add(qs.getResource("?x").getURI());
        }

        qe.close();
        return result;
    }

    /**
     * Reads the contents of an UTF-8 encoded file.
     * @param fileToRead The file that shall be read.
     * @return File contents.
     */
    static String readUtf8(File fileToRead){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead),
                StandardCharsets.UTF_8))){
            String readLine;
            while((readLine = reader.readLine()) != null){
                sb.append(readLine);
                sb.append("\n");
            }
        } catch (FileNotFoundException e) {
            LOGGER.info("File not found.", e);
        } catch (IOException e) {
            LOGGER.info("IOException occurred.", e);
        }
        return sb.toString();
    }


    private boolean sanityChecks(){
        if(!queryDirectory.exists()){
            LOGGER.error("The provided query directory does not exist. ABORTING program.");
            return false;
        }
        if(!queryDirectory.isDirectory()){
            LOGGER.error("The provided query directory is not a directory. ABORTING program.");
            return false;
        }
        if(queryDirectory.listFiles() == null || queryDirectory.listFiles().length == 0){
            LOGGER.error("The provided query directory is empty. ABORTING program.");
            return false;
        }
        if(generatedDirectory.exists()){
            LOGGER.error("The directoryToGenerate must not yet exist. ABORTING program.");
            return false;
        }
        return true;
    }


    /**
     * Returns the number of triples in the graph.
     * @return Returns the number of triples in the graph. In case of an error, a negative number is returned.
     */
    public int getNumberOfStatements() {
        try {
            QueryExecution qe = connection.query(QueryFactory.create("SELECT (COUNT(*) AS ?count) WHERE {?s ?p ?o}"));
            ResultSet rs = qe.execSelect();
            if (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Literal literal = qs.getLiteral("?count");
                String literalString = literal.getLexicalForm();
                return Integer.parseInt(literalString);
            } else {
                return -1;
            }
        } catch (Exception e) {
            LOGGER.error("An exception occurred while trying to run a SPARQL query.", e);
            return -1;
        }
    }

}
