package de.uni_mannheim.informatik.dws.dl_tc_generator.by_query;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.IGenerator;
import de.uni_mannheim.informatik.dws.dl_tc_generator.TrainTestSplit;
import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The following DBpedia files are required at the SPARQL endpoint:
 * <ul>
 *     <li></li>
 * </ul>
 */
public class GeneratorQuery implements IGenerator {


    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorQuery.class);

    /**
     * URL of the dataset. By default, using the Uni Mannheim endpoint.
     */
    public static final String DATASET_URL = "http://dws-04.informatik.uni-mannheim.de:3030/dbpedia-all-2021-09";

    /**
     * Name of the positive query file.
     */
    static final String POSITIVE_FILE_NAME = "positive_query.sparql";

    /**
     * Name of the negative query file.
     */
    static final String NEGATIVE_FILE_NAME = "negative_query.sparql";

    /**
     * Name of the hard negative query file.
     */
    static final String NEGATIVE_HARD_FILE_NAME = "negative_query_hard.sparql";

    /**
     * Directory where the queries reside.
     */
    private final File queryDirectory;

    /**
     * If this set contains entries, the {@link GeneratorQuery#generateTestCases()} method will only consider test case
     * collections (i.e., directories) with that name - for example, if there are tc1, tc2, ... and the set contains
     * tc1 and tc2, then only the latter two will be generated.
     */
    private Set<String> includeOnlyCollection;

    /**
     * If this set contains entries, the {@link GeneratorQuery#generateTestCases()} method will only consider test cases
     * with that name - for example, if there are cities, movies, people, ... and the set contains only "movies",
     * only the test cases named "movies" will be generated.
     */
    private Set<String> includeOnlyTestCase;

    /**
     * The separator for the data files (e.g. train.txt).
     */
    private String separator = Defaults.CSV_SEPARATOR;

    /**
     * Train-test split ratio.
     */
    private TrainTestSplit trainTestSplit = Defaults.TRAIN_TEST_SPLIT;

    /**
     * The generated directory must not exist yet.
     */
    private final File generatedDirectory;

    /**
     * SPARQL connection to be sued.
     */
    private final RDFConnection connection;

    /**
     * The sizes of the test cases.
     */
    private int[] sizes = Defaults.SIZES;

    /**
     * Default timeout in seconds
     */
    private int timeoutInSeconds = 300;

    /**
     * If true a random element is added to the queries so that there is some variation in the results
     * (to avoid situations in which the query returns persons all starting with the letter 'A' etc.).
     */
    private boolean isRandomizeResults = true;

    public GeneratorQuery(String queryDirectoryPath, String directoryToGeneratePath) {
        this(new File(queryDirectoryPath), new File(directoryToGeneratePath));
    }

    /**
     * Constructor
     *
     * @param queryDirectory      Directory containing query files.
     * @param directoryToGenerate Directory that shall be generated (must not exist yet).
     */
    public GeneratorQuery(File queryDirectory, File directoryToGenerate) {
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
        LOGGER.info("Starting test case generation.\nTimeout " + this.getTimeoutInSeconds() +
                "\nTrain-Test Split: " + this.getTrainTestSplit().trainSplit() + "-" + this.getTrainTestSplit().testSplit() +
                "\nQuery directory " + queryDirectory.getAbsolutePath() +
                "\nDirectory to be written " + generatedDirectory.getAbsolutePath());

        if(generatedDirectory.mkdirs()){
            LOGGER.info("Created directory: " + generatedDirectory.getAbsolutePath());
        }

        File[] queryDirectoryFiles = queryDirectory.listFiles();
        if(queryDirectoryFiles == null || queryDirectoryFiles.length == 0){
            LOGGER.error("The provided query directory is empty. ABORTING generation.");
            return;
        }

        for (File tcCollectionDirectory : queryDirectoryFiles) {

            if (includeOnlyCollection != null && includeOnlyCollection.size() > 0) {
                if (!includeOnlyCollection.contains(tcCollectionDirectory.getName())) {
                    continue;
                }
            }

            if (!tcCollectionDirectory.isDirectory()) {
                LOGGER.info("Skipping file: " + tcCollectionDirectory.getAbsolutePath());
                continue;
            }

            File[] collectionDirectoryFiles = tcCollectionDirectory.listFiles();
            if (collectionDirectoryFiles == null || collectionDirectoryFiles.length == 0) {
                LOGGER.info("Skipping empty directory: " + tcCollectionDirectory.getAbsolutePath());
                continue;
            }

            for (File tcDirectory : collectionDirectoryFiles) {

                if (includeOnlyTestCase != null && includeOnlyTestCase.size() > 0) {
                    if (!includeOnlyTestCase.contains(tcDirectory.getName())) {
                        continue;
                    }
                }

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

                    if (!resultsDir.toFile().exists()) {
                        if(resultsDir.toFile().mkdirs()){
                            LOGGER.info("Created directory " + resultsDir);
                        }
                    }

                    // positives
                    LOGGER.info("Generating positives.");
                    List<String> positives = getQueryResults(positiveQueryFile, size);
                    Path pathToWrite = Paths.get(resultsDir.toString(), "positives.txt");
                    File fileToWrite = pathToWrite.toFile();
                    Util.writeListToFile(fileToWrite, positives);

                    // negatives
                    LOGGER.info("Generating negatives.");
                    List<String> negatives = getQueryResults(negativeQueryFile, size);
                    pathToWrite = Paths.get(resultsDir.toString(), "negatives.txt");
                    fileToWrite = pathToWrite.toFile();
                    Util.writeListToFile(fileToWrite, negatives);

                    // hard negatives
                    boolean hasHardNegatives = negativeHardQueryFile.exists();
                    if (hasHardNegatives) {
                        LOGGER.info("Generating hard negatives.");
                        List<String> hardNegatives = getQueryResults(negativeHardQueryFile, size);
                        pathToWrite = Paths.get(resultsDir.toString(), "negatives_hard.txt");
                        fileToWrite = pathToWrite.toFile();
                        Util.writeListToFile(fileToWrite, hardNegatives);
                        Util.writeTrainTestFiles(positives, hardNegatives, resultsDir, trainTestSplit, separator,
                                true);
                    }

                    Util.writeTrainTestFiles(positives, negatives, resultsDir, trainTestSplit, separator, false);

                } // end of size loop
            }
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
        String query = Util.readUtf8(file);
        query = query.replace("<number>", "" + size);

        // bringing some randomness to the results
        query = query.replaceAll("}(?=[\\n\\s]*LIMIT)", "BIND(RAND() AS ?sortKey)\n} ORDER BY ?sortKey ");
        if(!query.contains("?sortKey")){
            LOGGER.error("The injection of the randomness component failed.");
        }
        List<String> result = getUrisFromQuery(query, file);

        if(result == null){
            LOGGER.error(
                    """
                    The query failed. A likely reason is the randomness function. Trying again without
                    the randomness function.
                    """);
            query = query.replace("ORDER BY RAND() LIMIT", "LIMIT");
            result = getUrisFromQuery(query, file);
        }
        return result;
    }

    /**
     * Run the specified query.
     * @param query The query to be executed with no placeholders.
     * @param file File from which the query was derived. Merely required for logging.
     * @return In the case of success a List of String URIs. In the case of error null.
     */
    private List<String> getUrisFromQuery(String query, File file){
        List<String> result = new ArrayList<>();
        QueryExecution qe;
        try {
            qe = connection.query(query);
        } catch (QueryParseException qpe) {
            LOGGER.error("The following query could not be parsed: \n" + query);
            LOGGER.error("The problematic query file: " + file.getAbsolutePath());
            return null;
        }

        qe.setTimeout(timeoutInSeconds, TimeUnit.SECONDS);

        try {
            ResultSet rs = qe.execSelect();

            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                result.add(qs.getResource("?x").getURI());
            }
            qe.close();
        } catch (QueryExceptionHTTP hte) {
            LOGGER.error("The following query led to an error:\n" + query);
            LOGGER.error("There was a query exception when running the query. Returning null.", hte);
            return null;
        }
        return result;
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
        File[] queryDirectoryFiles = queryDirectory.listFiles();
        if (queryDirectoryFiles == null || queryDirectoryFiles.length == 0) {
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

    public Set<String> getIncludeOnlyCollection() {
        return includeOnlyCollection;
    }

    public void setIncludeOnlyCollection(Set<String> includeOnlyCollection) {
        this.includeOnlyCollection = includeOnlyCollection;
    }

    /**
     * This method will override the current set.
     *
     * @param includeOnlyCollection Values for the set.
     */
    public void setIncludeOnlyCollection(String... includeOnlyCollection) {
        this.includeOnlyCollection = new HashSet<>();
        this.includeOnlyCollection.addAll(Arrays.stream(includeOnlyCollection).toList());
    }


    public Set<String> getIncludeOnlyTestCase() {
        return includeOnlyTestCase;
    }

    public void setIncludeOnlyTestCase(Set<String> includeOnlyTestCase) {
        this.includeOnlyTestCase = includeOnlyTestCase;
    }

    /**
     * This method will override the current set.
     *
     * @param includeOnlyTestCase List the (exclusive) test cases that shall be included in the evaluation, others
     *                            will be discarded.
     */
    public void setIncludeOnlyTestCase(String... includeOnlyTestCase) {
        this.includeOnlyTestCase = new HashSet<>();
        this.includeOnlyTestCase.addAll(Arrays.stream(includeOnlyTestCase).toList());
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public boolean isRandomizeResults() {
        return isRandomizeResults;
    }

    public void setRandomizeResults(boolean randomizeResults) {
        isRandomizeResults = randomizeResults;
    }

    public TrainTestSplit getTrainTestSplit() {
        return trainTestSplit;
    }

    public void setTrainTestSplit(TrainTestSplit trainTestSplit) {
        this.trainTestSplit = trainTestSplit;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
