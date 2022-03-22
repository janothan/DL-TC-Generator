package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 *
 *
 * {@code
 * SELECT DISTINCT ?x WHERE
 * {
 *     {
 *         ?x a dbo:City .
 *         ?x ?r1 ?z .
 *         ?z ?r2 dbr:New_York_City .
 *     }
 *     UNION
 *     {
 *         ?x a dbo:City .
 *         ?z ?y1 ?x .
 *         dbr:New_York_City ?y2 ?z .
 *     }
 * }
 * }
 */
public class Tc05GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc05GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc05GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc05GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc05GeneratorSyntheticOntology.class);

    @Override
    public String getTcId() {
        return "tc05";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {

        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            // determine target instance
            final String targetInstance = ontologyGenerator.getRandomInstanceId();

            String z = ontologyGenerator.getRandomTripleWithObject(targetInstance).subject;
            String x = ontologyGenerator.getRandomTripleWithObject(z).subject;

            // determine target type
            String targetType = ontologyGenerator.getInstanceType(x);

            // ensure we have enough instances of the target type
            ontologyGenerator.ensureEnoughInstancesOfType(targetType, 2*nodesOfInterest);

            Set<String> typeInstances = ontologyGenerator.getInstancesOfTypeTransitive(targetType);

            // forward positives x -> r1 -> z -> r2 -> targetInstance
            while(positives.size() < nodesOfInterest/2){
                Triple t1 = ontologyGenerator.getRandomTripleWithObject(targetInstance);


            }



        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

}
