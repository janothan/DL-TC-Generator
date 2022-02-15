package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.TripleDataSetMemory;
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
 * Pattern: (X E1 Y1) AND (X E1 Y2) AND (Y1 E2 N) AND (Y2 E2 N)  <br/>
 * }
 */
public class Tc11GeneratorSyntheticRandom extends TcGeneratorSyntheticRandom {


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc11GeneratorSyntheticRandom.class);

    public Tc11GeneratorSyntheticRandom(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc11GeneratorSyntheticRandom(File directory) {
        super(directory);
    }

    public Tc11GeneratorSyntheticRandom(String directory) {
        super(directory);
    }

    @Override
    public void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        Iterator<String> edgeIterator = edgeIds.iterator();
        final String targetEdge1 = edgeIterator.next();
        final String targetEdge2 = edgeIterator.next();
        final String targetNode = nodeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                Triple triple1 = generateTriple(nodeIds, edgeIds);
                Triple triple2 = generateTripleWithStartNode(triple1.object, nodeIds, edgeIds);

                if (triple1.predicate.equals(targetEdge1)) {
                    if (triple2.predicate.equals(targetEdge2)) {
                        if (triple2.object.equals(targetNode)) {

                            // now we check whether there is another fitting object
                            Set<Triple> triples =
                                    graph.getObjectTriplesWithSubjectPredicate(triple1.subject, triple1.predicate);
                            if (triples != null) {
                                Set<String> objects = TripleDataSetMemory.getObjectsFromTripleSet(triples);

                                for (String o : objects) {
                                    // lastly, we check whether there is a triple O targetEdge2 targetNode
                                    if (graph.getAllObjectTriples().contains(
                                            new Triple(o, targetEdge2, targetNode))
                                    ) {
                                        positives.add(triple1.subject);
                                    }
                                }
                            }
                        }
                    }
                }

                writer.write(triple1.subject + " " + triple1.predicate + " " + triple1.object + ". \n");
                writer.write(triple2.subject + " " + triple2.predicate + " " + triple2.object + ". \n");
                graph.addObjectTriple(triple2);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    @Override
    public String getTcId() {
        return "TC11";
    }
}
