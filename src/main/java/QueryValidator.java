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
 */
public class QueryValidator {


    public QueryValidator(File queryDirectory){
        this.queryDirectory = queryDirectory;
        // must be initialized here since used in multiple methods
        connection = RDFConnection.connect(Generator.DATASET_URL);
    }

    public QueryValidator(String queryDirectory){
        this(new File(queryDirectory));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryValidator.class);

    private File queryDirectory;
    private RDFConnection connection;
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

    public String calculateCountReport() {
        StringBuffer sb = new StringBuffer();

        for (File tcCollectionDirectory : queryDirectory.listFiles()) {

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

                File positiveQueryFile = new File(tcDirectory, Generator.POSITIVE_FILE_NAME);
                File negativeQueryFile = new File(tcDirectory, Generator.NEGATIVE_FILE_NAME);
                File negativeHardQueryFile = new File(tcDirectory, Generator.NEGATIVE_HARD_FILE_NAME);

                if(positiveQueryFile.exists()){
                    int count = getQueryCounts(positiveQueryFile);
                    if(count >= 0) {
                        sb.append("\t").append("# Positives: ").append(count).append("\n");
                    } else {
                        sb.append("\t").append("ERROR: Problem with positive query.").append("\n");
                    }
                } else {
                    sb.append("\t").append("ERROR: Positive query file not found.").append("\n");
                }

                if(negativeQueryFile.exists()){
                    int count = getQueryCounts(negativeQueryFile);
                    if(count >= 0) {
                        sb.append("\t").append("# Negatives: ").append(count).append("\n");
                    } else {
                        sb.append("\t").append("ERROR: Problem with negative query.").append("\n");
                    }
                } else {
                    sb.append("\t").append("ERROR: Negative query file not found.").append("\n");
                }

                if(negativeHardQueryFile.exists()){
                    int count = getQueryCounts(negativeHardQueryFile);
                    if(count >= 0) {
                        sb.append("\t").append("# Hard Negatives: ").append(count).append("\n");
                    } else {
                        sb.append("\t").append("ERROR: Problem with hard negative query.").append("\n");
                    }
                } else {
                    sb.append("\t").append("WARN: Hard negative query file not found.").append("\n");
                }

            }
        }

        return sb.toString();
    }


    public int getQueryCounts(File file){
        String query = Util.readUtf8(file);
        int result = -1;

        final String limitPart = "LIMIT <number>";
        if(query.contains(limitPart)){
            query = query.replace(limitPart, "");
        } else {
            LOGGER.error("Missing " + limitPart + " in file: " + file.getAbsolutePath());
        }

        final String selectPart = "DISTINCT ?x";
        if(query.contains(selectPart)){
            query = query.replace(selectPart, "(COUNT(?x) AS ?num)");
        } else {
            LOGGER.error("Missing " + selectPart + " in file: " + file.getAbsolutePath());
            return result;
        }

        QueryExecution qe;
        try {
            qe = connection.query(query);
        } catch (QueryParseException qpe){
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
        } catch (QueryExceptionHTTP hte){
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
     * @return Values for set.
     */
    public void setIncludeOnlyTestCase(String... includeOnlyTestCase) {
        this.includeOnlyTestCase = new HashSet<>();
        this.includeOnlyTestCase.addAll(Arrays.stream(includeOnlyTestCase).toList());
    }

    public static void main(String[] args) throws Exception {
        QueryValidator validator = new QueryValidator(
                new File(QueryValidator.class.getResource("queries").toURI()));
        validator.setTimeoutInSeconds(350);
        validator.setIncludeOnlyCollection("tc5");
        //validator.setIncludeOnlyTestCase("people");
        String report = validator.calculateCountReport();
        System.out.println(report);
    }

}
