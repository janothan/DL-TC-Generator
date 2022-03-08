package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
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
 * Named Nodes: Z<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (Y E1 X) AND (Y E2 Z) <br/>
 * }
 */
public class Tc08GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc08GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc08GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc08GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc08GeneratorSyntheticConstructed.class);


    @Override
    public String getTcId() {
        return "TC08";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int avgTriplesPerNode) {
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
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                final String positive = Util.randomDrawFromSet(nodeIds);
                writer.write(randomSubject + " " + targetEdge1 + " " + positive + " . \n");
                writer.write(randomSubject + " " + targetEdge2 + " " + targetNode + " . \n");
                graph.addObjectTriple(new Triple(randomSubject, targetEdge1, positive));
                graph.addObjectTriple(new Triple(randomSubject, targetEdge2, targetNode));
                positives.add(positive);
            }

            // let's add random triples / write negatives
            for (String node : nodeIds) {

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);
                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple1 = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    Triple triple2 = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    if(isAccidentallyPositive(triple1, triple2, targetEdge1, targetEdge2, targetNode, positives, graph)){
                        i--;
                    } else {
                        writer.write(triple1.subject + " " + triple1.predicate + " " + triple1.object + " . \n");
                        writer.write(triple2.subject + " " + triple2.predicate + " " + triple2.object + " . \n");
                        graph.addObjectTriple(triple1);
                        graph.addObjectTriple(triple2);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }


    public static boolean isAccidentallyPositive(Triple triple1, Triple triple2, String targetEdge1, String targetEdge2,
                                                 String targetNode, Set<String> positives, TripleDataSetMemory graph) {

        boolean r1 = isPositiveCompositionOneSide(triple1, triple2, targetEdge1, targetEdge2, targetNode, positives);
        if (r1) return true;

        boolean r2 = isPositiveCompositionOneSide(triple2, triple1, targetEdge1, targetEdge2, targetNode, positives);
        if (r2) return true;

        boolean r3 = isAccidentallyPositiveOneSide(triple1, targetEdge1, targetEdge2, targetNode, positives, graph);
        if (r3) return true;
        return isAccidentallyPositiveOneSide(triple2, targetEdge1, targetEdge2, targetNode, positives, graph);
    }

    static boolean isAccidentallyPositiveOneSide(Triple triple, String targetEdge1, String targetEdge2,
                                                 String targetNode, Set<String> positives, TripleDataSetMemory graph) {

        // if we generate (Y E1 X), make sure that there is no (Y E2 Z)
        if (
                triple.predicate.equals(targetEdge1)
                        && graph.getAllObjectTriples().contains(new Triple(triple.subject, targetEdge2, targetNode))
        ) return true;

        // if we generate (Y E2 Z), make sure that there is no (Y E1 X)
        if (triple.predicate.equals(targetEdge2) && triple.predicate.equals(targetNode)) {
            Set<String> objects =
                    TripleDataSetMemory.getObjectsFromTripleSet(graph.getObjectTriplesWithSubjectPredicate(triple.subject,
                            targetEdge1));

            for (String object : objects) {
                // if there is a single non-positive: abort
                if (!positives.contains(object)) {
                    return true;
                }
            }
        }
        return false;
    }


    static boolean isPositiveCompositionOneSide(Triple triple1, Triple triple2, String targetEdge1, String targetEdge2,
                                                String targetNode, Set<String> positives) {
        return triple1.subject.equals(triple2.subject)
                && !positives.contains(triple1.subject)
                && triple1.predicate.equals(targetEdge1)
                && triple2.predicate.equals(targetEdge2)
                && triple2.object.equals(targetNode);
    }

}
