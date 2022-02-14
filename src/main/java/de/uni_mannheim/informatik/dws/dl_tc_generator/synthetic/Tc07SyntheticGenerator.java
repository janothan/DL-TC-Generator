package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: N<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (X E1 Y E2 N) <br/>
 * }
 */
public class Tc07SyntheticGenerator extends SyntheticGenerator{


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc07SyntheticGenerator.class);

    public Tc07SyntheticGenerator(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc07SyntheticGenerator(File directory) {
        super(directory);
    }

    public Tc07SyntheticGenerator(String directory) {
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
        final String targetNode = nodeIds.iterator().next();
        Iterator<String> edgeIterator = edgeIds.iterator();
        final String targetEdge1 = edgeIterator.next();
        final String targetEdge2 = edgeIterator.next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                Triple triple1 = generateTriple(nodeIds, edgeIds);
                Triple triple2 = generateTripleWithStartNode(triple1.object, nodeIds, edgeIds);

                if(triple1.predicate.equals(targetEdge1)
                        && triple2.predicate.equals(targetEdge2)
                        && triple2.object.equals(targetNode)
                ) {
                    positives.add(triple1.subject);
                }

                writer.write(triple1.subject + " " + triple1.predicate + " " + triple1.object + ". \n");
                writer.write(triple2.subject + " " + triple2.predicate + " " + triple2.object + ". \n");
                graph.addObjectTriple(triple1);
                graph.addObjectTriple(triple2);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    @Override
    String getTcId() {
        return "TC07";
    }
}
