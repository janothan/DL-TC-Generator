package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: -<br/>
 * Named Edges: E<br/>
 * Pattern: (Y E X) AND (Z E X) <br/>
 * }
 */
public class Tc10GeneratorSyntheticRandom extends TcGeneratorSyntheticRandom {


    public Tc10GeneratorSyntheticRandom(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc10GeneratorSyntheticRandom(File directory) {
        super(directory);
    }

    public Tc10GeneratorSyntheticRandom(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc10GeneratorSyntheticRandom.class);

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest,
                                             int totalEdges) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        final String targetEdge = edgeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                Triple triple = generateTriple(nodeIds, edgeIds);

                if(triple.predicate.equals(targetEdge)) {
                    Set<Triple> result = graph.getObjectTriplesWithPredicateObject(triple.predicate, triple.object);
                    if(result != null
                            && result.size() >= 1
                            && !result.iterator().next().subject.equals(triple.subject)) {
                        positives.add(triple.object);
                    }
                }

                writer.write(triple.subject + " " + triple.predicate + " " + triple.object + ". \n");
                graph.addObjectTriple(triple);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    @Override
    public String getTcId() {
        return "TC10";
    }
}
