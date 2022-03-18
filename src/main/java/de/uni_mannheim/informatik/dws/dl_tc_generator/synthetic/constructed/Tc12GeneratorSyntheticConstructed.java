package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.TripleDataSetMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * TC 12 Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: N<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (Y1 E1 X) AND (Y1 E2 N) AND (Y2 E1 X) AND (Y2 E2 N)  <br/>
 * }
 */
public class Tc12GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc12GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc12GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc12GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc12GeneratorSyntheticConstructed.class);

    @Override
    public String getTcId() {
        return "TC12";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {
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
            // generate positives
            while (positives.size() < nodesOfInterest) {
                final String randomNode1 = Util.randomDrawFromSet(nodeIds);
                final String randomNode2 = Util.randomDrawFromSet(nodeIds);
                final String positive = Util.randomDrawFromSet(nodeIds);

                if (randomNode1.equals(randomNode2) || randomNode1.equals(positive) || randomNode2.equals(positive)) {
                    // just redraw
                    continue;
                }

                writer.write(randomNode1 + " " + targetEdge1 + " " + positive + " . \n");
                writer.write(randomNode1 + " " + targetEdge2 + " " + targetNode + " . \n");

                writer.write(randomNode2 + " " + targetEdge1 + " " + positive + " . \n");
                writer.write(randomNode2 + " " + targetEdge2 + " " + targetNode + " . \n");

                graph.addObjectTriple(new Triple(randomNode1, targetEdge1, positive));
                graph.addObjectTriple(new Triple(randomNode1, targetEdge2, targetNode));
                graph.addObjectTriple(new Triple(randomNode2, targetEdge1, positive));
                graph.addObjectTriple(new Triple(randomNode2, targetEdge2, targetNode));
                positives.add(positive);
            }


            // generating negatives randomly
            for (String node : nodeIds) {

                // draw number of triples
                int tripleNumber = random.nextInt(this.maxTriplesPerNode + 1);
                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = generateTripleWithStartNode(node, nodeIds, edgeIds);

                    if(isAccidentallyPositive(triple, targetEdge1, targetEdge2, targetNode, graph)){
                        i--;
                    } else {
                        writer.write(triple.subject + " " + triple.predicate + " " + triple.object + " . \n");
                        graph.addObjectTriple(triple);
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    public static boolean isAccidentallyPositive(Triple triple, String targetEdge1,
                                                 String targetEdge2, String targetNode,
                                                 TripleDataSetMemory graph) {

        if (triple.predicate.equals(targetEdge2) && triple.object.equals(targetNode)) {
            // case 1: we generated a Y1 E2 N

            Set<String> potentialXs = TripleDataSetMemory.getObjectsFromTripleSet(
                    graph.getObjectTriplesWithSubjectPredicate(triple.subject, targetEdge1)
            );
            if (potentialXs != null) {

                for (String potentialX : potentialXs) {

                    // get potential y2
                    Set<String> potentialY2s = TripleDataSetMemory.getSubjectsFromTripleSet(
                            graph.getObjectTriplesWithPredicateObject(targetEdge1, potentialX)
                    );
                    if (potentialY2s != null) {
                        for (String potentialY2 : potentialY2s) {
                            Set<Triple> y2e2triples =
                                    graph.getObjectTriplesWithSubjectPredicate(potentialY2,
                                            targetEdge2);
                            if (y2e2triples != null) {
                                for (Triple t : y2e2triples) {
                                    if (t.object.equals(targetNode)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (triple.predicate.equals(targetEdge1)) {
            // we generated a Y1 E1 X

            // check for Y1 E2 N
            if (graph.getAllObjectTriples().contains(
                    new Triple(triple.subject, targetEdge2, targetNode)
            )) {

                // check whether there is a Y2
                Set<String> potentialY2s = TripleDataSetMemory.getSubjectsFromTripleSet(
                        graph.getObjectTriplesWithPredicateObject(targetEdge1, triple.object)
                );

                if (potentialY2s != null) {

                    for (String potentialY2 : potentialY2s) {
                        if (graph.getAllObjectTriples().contains(
                                new Triple(potentialY2, targetEdge2, targetNode))
                        ) {
                            return true;
                        }
                    }

                }
            }

        }
        return false;
    }

}
