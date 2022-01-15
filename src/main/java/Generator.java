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
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * Constructor
     *
     * @param queryDirectory      Directory containing query files.
     * @param directoryToGenerate Directory that shall be generated (must not exist yet).
     */
    public Generator(File queryDirectory, File directoryToGenerate) {
        this.queryDirectory = queryDirectory;
        this.generatedDirectory = directoryToGenerate;

        if (!sanityChecks()) {
            System.exit(1);
        }

        // must be initialized here since used in multiple methods
        connection = RDFConnection.connect(DATASET_URL);
    }

    /**
     * Generate the actual test cases.
     */
    public void generateTestCases() {
        generatedDirectory.mkdirs();

        for (File tcCollectionDirectory : queryDirectory.listFiles()) {
            if (!tcCollectionDirectory.isDirectory()) {
                LOGGER.info("Skipping file: " + tcCollectionDirectory.getAbsolutePath());
                continue;
            }

            File[] collectionDirectoryFiles = tcCollectionDirectory.listFiles();
            if(collectionDirectoryFiles == null || collectionDirectoryFiles.length == 0){
                LOGGER.info("Skipping empty directory: " + tcCollectionDirectory.getAbsolutePath());
                continue;
            }

            for (File tcDirectory : collectionDirectoryFiles) {
                if (!tcDirectory.isDirectory()) {
                    LOGGER.info("Skipping file: " + tcDirectory.getAbsolutePath());
                }
                File[] tcFiles = tcDirectory.listFiles();
                if (tcFiles == null || tcFiles.length == 0) {
                    LOGGER.info("Skipping empty directory: " + tcDirectory.getAbsolutePath());
                    continue;
                }

                Set<String> fileNames = Arrays.stream(tcFiles).map(File::getName).collect(Collectors.toSet());
                if (!fileNames.contains(POSITIVE_FILE_NAME)) {
                    LOGGER.info("Directory (" + tcDirectory.getAbsolutePath() + ") is missing file '" + POSITIVE_FILE_NAME +
                            "'. Skipping directory.");
                    continue;
                }
                if (!fileNames.contains(NEGATIVE_FILE_NAME)) {
                    LOGGER.info("Directory (" + tcDirectory.getAbsolutePath() + ") is missing file '" + NEGATIVE_FILE_NAME +
                            "'. Skipping directory.");
                    continue;
                }

                File positiveQueryFile = new File(tcDirectory, POSITIVE_FILE_NAME);
                File negativeQueryFile = new File(tcDirectory, NEGATIVE_FILE_NAME);
                File negativeHardQueryFile = new File(tcDirectory, NEGATIVE_HARD_FILE_NAME);
                for (int size : sizes) {
                    LOGGER.info("Processing " + tcCollectionDirectory.getName() + "-" + tcDirectory.getName()
                            + " (" + size + ")");

                    Path resultsDir = Paths.get(generatedDirectory.getAbsolutePath(),
                            tcCollectionDirectory.getName(),
                            tcDirectory.getName(),
                            "" + size);

                    if(!resultsDir.toFile().exists()){
                        resultsDir.toFile().mkdirs();
                    }

                    // positives
                    List<String> queryResults = getQueryResults(positiveQueryFile, size);
                    Path pathToWrite = Paths.get(resultsDir.toString(), "positives.txt");
                    File fileToWrite = pathToWrite.toFile();
                    writeListToFile(fileToWrite, queryResults);

                    // negatives
                    queryResults = getQueryResults(negativeQueryFile, size);
                    pathToWrite = Paths.get(resultsDir.toString(), "negatives.txt");
                    fileToWrite = pathToWrite.toFile();
                    writeListToFile(fileToWrite, queryResults);

                    // hard negatives
                    if(negativeHardQueryFile.exists()) {
                        queryResults = getQueryResults(negativeHardQueryFile, size);
                        pathToWrite = Paths.get(resultsDir.toString(), "negatives_hard.txt");
                        fileToWrite = pathToWrite.toFile();
                        writeListToFile(fileToWrite, queryResults);
                    }
                }

            }
        }
    }

    /**
     * Persisted in UTF-8.
     *
     * @param fileToWrite File that shall be written.
     * @param listToWrite List that shall be written.
     */
    public static void writeListToFile(File fileToWrite, List<String> listToWrite) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite), StandardCharsets.UTF_8))) {
            for (String line : listToWrite) {
                writer.write(line);
                writer.write("\n");
            }
        } catch (FileNotFoundException fnfe) {
            LOGGER.error(
                    "An error occurred while trying to persist file '" + fileToWrite.getAbsolutePath() + "'.",
                    fnfe);
        } catch (IOException e) {
            LOGGER.error("An IO exception occurred while trying to write file '" + fileToWrite.getAbsolutePath() +
                    "'.", e);
        }
    }


    /**
     * Run a query given a file.
     *
     * @param file The .sparql file that contains the query (with an optional {@code <number>} placeholder). The file
     *             must be utf-8 encoded.
     * @param size The number of desired results.
     * @return A list of URIs.
     */
    public List<String> getQueryResults(File file, int size) {
        String query = readUtf8(file);
        query = query.replace("<number>", "" + size);
        List<String> result = new ArrayList<>();

        QueryExecution qe = connection.query(query);
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            result.add(qs.getResource("?x").getURI());
        }

        qe.close();
        return result;
    }

    /**
     * Reads the contents of an UTF-8 encoded file.
     *
     * @param fileToRead The file that shall be read.
     * @return File contents.
     */
    static String readUtf8(File fileToRead) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead),
                StandardCharsets.UTF_8))) {
            String readLine;
            while ((readLine = reader.readLine()) != null) {
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


    private boolean sanityChecks() {
        if (!queryDirectory.exists()) {
            LOGGER.error("The provided query directory does not exist. ABORTING program.");
            return false;
        }
        if (!queryDirectory.isDirectory()) {
            LOGGER.error("The provided query directory is not a directory. ABORTING program.");
            return false;
        }
        if (queryDirectory.listFiles() == null || queryDirectory.listFiles().length == 0) {
            LOGGER.error("The provided query directory is empty. ABORTING program.");
            return false;
        }
        if (generatedDirectory.exists()) {
            LOGGER.error("The directoryToGenerate must not yet exist. ABORTING program.");
            return false;
        }
        return true;
    }


    /**
     * Returns the number of triples in the graph.
     *
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

    public int[] getSizes() {
        return sizes;
    }

    public void setSizes(int[] sizes) {
        this.sizes = sizes;
    }
}
