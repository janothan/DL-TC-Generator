import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdfconnection.RDFConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generator {


    private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);
    private static final String DATASET_URL = "http://dws-04.informatik.uni-mannheim.de:3030/dbpedia-all-2021-09";
    RDFConnection connection;

    public Generator(){
        connection = RDFConnection.connect(DATASET_URL);
    }

    public static void main(String[] args) {
        LOGGER.info("HELLO");
        Generator generator = new Generator();
        System.out.println(generator.getNumberOfStatements());
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
