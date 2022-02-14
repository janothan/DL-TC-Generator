package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.TripleDataSetMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: N<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (Y1 E1 X) AND (Y1 E2 N) AND (Y2 E1 X) AND (Y2 E2 N)  <br/>
 * }
 */
public class Tc12SyntheticGenerator extends SyntheticGenerator {


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc12SyntheticGenerator.class);

    public Tc12SyntheticGenerator(String directory) {
        super(directory);
    }

    public Tc12SyntheticGenerator(File directory) {
        super(directory);
    }

    public Tc12SyntheticGenerator(File directory, int[] sizes) {
        super(directory, sizes);
    }

    @Override
    void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {
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
                Triple triple2 = generateTripleWithStartNode(triple1.subject, nodeIds, edgeIds);

                if (triple1.predicate.equals(targetEdge1)
                    && triple2.predicate.equals(targetEdge2)
                    && triple2.object.equals(targetNode)
                ) {
                    // Y1: triple2.subject is ok.
                    // Next we check whether there is also a Y2.

                    Set<Triple> triples = graph.getObjectTriplesWithPredicateObject(targetEdge1, triple1.object);
                    if(triples != null){
                        // we now have some potential Y2
                        // we need to check whether Y2 E2 N
                        Set<String> subjects = TripleDataSetMemory.getSubjectsFromTripleSet(triples);
                        for(String s : subjects){
                            if(graph.getAllObjectTriples().contains(
                                    new Triple(s,targetEdge2, targetNode)
                            )){
                                positives.add(triple1.object);
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
    String getTcId() {
        return "TC12";
    }
}
