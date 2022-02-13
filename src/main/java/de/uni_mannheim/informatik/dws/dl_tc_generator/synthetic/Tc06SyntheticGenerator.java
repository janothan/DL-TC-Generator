package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

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
 * Named Nodes: N<br/>
 * Named Edges: E<br/>
 * Pattern: (X E N) <br/>
 * }
 */
public class Tc06SyntheticGenerator extends SyntheticGenerator {


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc06SyntheticGenerator.class);

    public Tc06SyntheticGenerator(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc06SyntheticGenerator(File directory) {
        super(directory);
    }

    public Tc06SyntheticGenerator(String directory) {
        super(directory);
    }

    @Override
    void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        String targetEdge = edgeIds.iterator().next();
        String targetNode = nodeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                Triple triple = generateTriple(nodeIds, edgeIds);

                if(triple.predicate.equals(targetEdge) && triple.object.equals(targetNode)) {
                    positives.add(triple.subject);
                }
                writer.write(triple.subject + " " + triple.predicate + " " + triple.object + ". \n");
                graph.addObjectTriple(triple);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    @Override
    String getTcId() {
        return "TC06";
    }
}
