package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;


import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.jrdf2vec.util.Util.randomDrawFromSet;


/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: -<br/>
 * Named Edges: E<br/>
 * Pattern: X E Y<br/>
 * }
 */
public class Tc01SyntheticGenerator extends SyntheticGenerator {


    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc01SyntheticGenerator.class);


    /**
     * Default Constructor
     *
     * @param directory The directory to be created. The directory must not exist yet.
     * @param sizes     The sizes to be evaluated.
     */
    public Tc01SyntheticGenerator(File directory, int[] sizes) {
        super(directory, sizes);
    }

    /**
     * Convenience Constructor.
     *
     * @param directory The directory to be created. The directory must not exist yet.
     */
    public Tc01SyntheticGenerator(File directory) {
        super(directory);
    }

    /**
     * Convenience constructor.
     * @param directory The directory to be created. The directory must not exist yet.
     */
    public Tc01SyntheticGenerator(String directory) {
        super(directory);
    }

    /**
     * Write the graph to the fileToBeWritten. This method also fills the {@link Tc01SyntheticGenerator#positives}.
     *
     * @param fileToBeWritten The file that shall be written (must not exist yet).
     * @param totalNodes      The total number of nodes. The actual graph may not contain the full number.
     * @param nodesOfInterest The number of desired positives.
     * @param totalEdges      The number of edges. The actual graph may not contain the full number.
     */
    public void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        String targetEdge = edgeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                Triple triple = generateTriple(nodeIds, edgeIds);
                if (triple.predicate.equals(targetEdge)) {
                    positives.add(triple.subject);
                }
                writer.write(triple.subject + " " + triple.predicate + " " + triple.object + ". \n");
                graph.addObjectTriple(triple);
            }

            // in some cases we end up with too few negatives, we generate additional negatives in this case
            int currentNegativesSize = getNegatives().size();
            if (currentNegativesSize < nodesOfInterest) {
                LOGGER.warn("Insufficient amount of negatives. Creating negatives artificially.");
                final int delta = nodesOfInterest - currentNegativesSize;
                Set<String> newNodeIds = addAdditionalNodeIds(nodeIds, delta);
                while (getNegatives().size() < nodesOfInterest) {
                    String p = randomDrawFromSet(edgeIds);
                    if (p.equals(targetEdge)) {
                        continue;
                    }
                    String s = randomDrawFromSet(newNodeIds);
                    String o = randomDrawFromSet(nodeIds);
                    writer.write(s + " " + p + " " + o + " . \n");
                    graph.addObjectTriple(new Triple(s, p, o));

                    // wire our subject back in the graph
                    String s2 = randomDrawFromSet(nodeIds);
                    String p2 = randomDrawFromSet(edgeIds);
                    writer.write(s2 + " " + p2 + " " + s + " . \n");
                    graph.addObjectTriple(new Triple(s2, p2, o));
                }
            }

        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    @Override
    String getTcId() {
        return "TC01";
    }

}
