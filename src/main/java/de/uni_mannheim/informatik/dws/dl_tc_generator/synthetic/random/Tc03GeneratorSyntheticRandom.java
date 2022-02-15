package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;
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
 * Pattern: (X E Y) OR (Y E X) <br/>
 * }
 */
public class Tc03GeneratorSyntheticRandom extends TcGeneratorSyntheticRandom {


    /**
     * Convenience Constructor.
     * @param directory The directory to be created. The directory must not exist yet.
     */
    public Tc03GeneratorSyntheticRandom(File directory){
        super(directory);
    }

    /**
     * Convenience constructor.
     * @param directory The directory to be created. The directory must not exist yet.
     */
    public Tc03GeneratorSyntheticRandom(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc03GeneratorSyntheticRandom.class);

    /**
     * Write the graph to the fileToBeWritten. This method also fills the {@link TcGeneratorSynthetic#positives}.
     * @param fileToBeWritten The file that shall be written (must not exist yet).
     * @param totalNodes The total number of nodes. The actual graph may not contain the full number.
     * @param nodesOfInterest The number of desired positives.
     * @param totalEdges The number of edges. The actual graph may not contain the full number.
     */
    public void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges){
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
                    positives.add(triple.subject);
                    positives.add(triple.object);
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
        return "TC03";
    }
}
