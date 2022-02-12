package de.uni_mannheim.informatik.dws.dl_tc_generator.by_query;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A class to check the queries and to calculate some statistics.
 * Note that this class fires queries to the endpoint (must be accessible).
 */
public class QueryValidator {


    public QueryValidator(File queryDirectory) {
        this.queryDirectory = queryDirectory;
        // must be initialized here since used in multiple methods
        connection = RDFConnection.connect(GeneratorQuery.DATASET_URL);
    }

    public QueryValidator(String queryDirectory) {
        this(new File(queryDirectory));
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryValidator.class);

    private final File queryDirectory;
    private final RDFConnection connection;
    private int timeoutInSeconds = 60;

    /**
     * If this set contains entries, the methods will only consider test case
     * collections (i.e., directories) with that name - for example, if there are tc1, tc2, ... and the set contains
     * tc1 and tc2, then only the latter two will be generated.
     */
    private Set<String> includeOnlyCollection;

    /**
     * If this set contains entries, the methods will only consider test cases
     * with that name - for example, if there are cities, movies, people, ... and the set contains only "movies",
     * only the test cases named "movies" will be generated.
     */
    private Set<String> includeOnlyTestCase;

    public String calculateCountReport(boolean isRunCountQueries) {
        StringBuilder sb = new StringBuilder();

        File[] queryDirectoryFiles = queryDirectory.listFiles();
        if (queryDirectoryFiles == null) {
            return "ERROR: The query directory does not contain anything.";
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

            String collectionName = tcCollectionDirectory.getName();
            LOGGER.info("Processing " + collectionName);

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

                String tcName = tcDirectory.getName();
                LOGGER.info("Processing " + collectionName + "-" + tcName);

                sb.append(collectionName).append("-").append(tcName).append("\n");

                File positiveQueryFile = new File(tcDirectory, GeneratorQuery.POSITIVE_FILE_NAME);
                File negativeQueryFile = new File(tcDirectory, GeneratorQuery.NEGATIVE_FILE_NAME);
                File negativeHardQueryFile = new File(tcDirectory, GeneratorQuery.NEGATIVE_HARD_FILE_NAME);

                if (positiveQueryFile.exists()) {
                    if(isRunCountQueries) {
                        int count = getQueryCounts(positiveQueryFile);
                        if (count >= 0) {
                            sb.append("\t").append("# Positives: ").append(count).append("\n");
                        } else {
                            sb.append("\t").append("ERROR: Problem with positive query.").append("\n");
                        }
                    }
                    if(!isLimitNumberOk(positiveQueryFile)){
                        sb.append("\t").append("ERROR: LIMIT <number> missing in positive query.").append("\n");
                    }
                } else {
                    sb.append("\t").append("ERROR: Positive query file not found.").append("\n");
                }

                if (negativeQueryFile.exists()) {
                    if(isRunCountQueries) {
                        int count = getQueryCounts(negativeQueryFile);
                        if (count >= 0) {
                            sb.append("\t").append("# Negatives: ").append(count).append("\n");
                        } else {
                            sb.append("\t").append("ERROR: Problem with negative query.").append("\n");
                        }
                    }
                    if(!isLimitNumberOk(negativeQueryFile)){
                        sb.append("\t").append("ERROR: LIMIT <number> missing in negative query.").append("\n");
                    }
                } else {
                    sb.append("\t").append("ERROR: Negative query file not found.").append("\n");
                }

                if (negativeHardQueryFile.exists()) {
                    if(isRunCountQueries) {
                        int count = getQueryCounts(negativeHardQueryFile);
                        if (count >= 0) {
                            sb.append("\t").append("# Hard Negatives: ").append(count).append("\n");
                        } else {
                            sb.append("\t").append("ERROR: Problem with hard negative query.").append("\n");
                        }
                    }
                    if(!isLimitNumberOk(negativeHardQueryFile)){
                        sb.append("\t").append("ERROR: LIMIT <number> missing in hard negative query.").append("\n");
                    }
                } else {
                    sb.append("\t").append("WARN: Hard negative query file not found.").append("\n");
                }

            }
        }
        return sb.toString();
    }

    /**
     * Checks whether the limit number statement appears in the query.
     * @param file The query file to be checked.
     * @return True if everything is ok, false if {@code LIMIT <number>} statement is missing.
     */
    static boolean isLimitNumberOk(File file){
        String query = Util.readUtf8(file);
        final String limitRegex = "LIMIT[\\s\\n\\r]*<number>";
        String queryOld = query;
        query = query.replaceAll(limitRegex, "");
        if (query.equals(queryOld)) {
            LOGGER.error("Missing 'LIMIT <number>' part in file: " + file.getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     * The method obtains the number of results of the provided query (whereby the query is provided via file).
     *
     * @param file The file containing the query.
     * @return The number of results of the SPARQL query.
     */
    public int getQueryCounts(File file) {
        String query = Util.readUtf8(file);
        int result = -1;

        final String limitRegex = "LIMIT[\\s\\n\\r]*<number>";
        String queryOld = query;
        query = query.replaceAll(limitRegex, "");
        if (query.equals(queryOld)) {
            LOGGER.error("Missing 'LIMIT <number>' part in file: " + file.getAbsolutePath());
            return result;
        }

        final String selectRegex = "DISTINCT[\\s\\r\\n]*\\?x";
        queryOld = query;
        query = query.replaceAll(selectRegex, "(COUNT(?x) AS ?num)");

        if (queryOld.equals(query)) {
            LOGGER.error("Missing 'DISTINCT ?x' part in file: " + file.getAbsolutePath());
            return result;
        }

        QueryExecution qe;
        try {
            qe = connection.query(query);
        } catch (QueryParseException qpe) {
            LOGGER.error("The following query could not be parsed: \n" + query);
            LOGGER.error("The problematic query file: " + file.getAbsolutePath());
            return result;
        }

        qe.setTimeout(timeoutInSeconds, TimeUnit.SECONDS);

        try {
            ResultSet rs = qe.execSelect();
            if (rs.hasNext()) {
                QuerySolution qs = rs.next();
                result = qs.getLiteral("?num").getInt();
            }
            qe.close();
        } catch (QueryExceptionHTTP hte) {
            LOGGER.error("There was a query exception when running the query. Returning an empty list.");
        }
        return result;
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public Set<String> getIncludeOnlyCollection() {
        return includeOnlyCollection;
    }

    public void setIncludeOnlyCollection(Set<String> includeOnlyCollection) {
        this.includeOnlyCollection = includeOnlyCollection;
    }

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
     * @param includeOnlyTestCase Values for set.
     */
    public void setIncludeOnlyTestCase(String... includeOnlyTestCase) {
        this.includeOnlyTestCase = new HashSet<>();
        this.includeOnlyTestCase.addAll(Arrays.stream(includeOnlyTestCase).toList());
    }


    public static void main(String[] args) throws Exception {
        QueryValidator validator = new QueryValidator(
                new File("/Users/janportisch/IdeaProjects/DL-TC-Generator/src/main/resources/queries"));
        validator.setTimeoutInSeconds(350);
        //validator.setIncludeOnlyCollection("tc5");
        //validator.setIncludeOnlyTestCase("people");
        String report = validator.calculateCountReport(false);
        System.out.println(report);
    }

}
