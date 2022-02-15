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
 * Named Nodes: N<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (X E1 Y E2 N) <br/>
 * }
 */
public class Tc07GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc07GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc07GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc07GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc07GeneratorSyntheticConstructed.class);

    @Override
    public String getTcId() {
        return "TC07";
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
                final String randomObject = Util.randomDrawFromSet(nodeIds);
                writer.write(randomSubject + " " + targetEdge1 + " " + randomObject + ". \n");
                writer.write(randomObject + " " + targetEdge2 + " " + targetNode + ". \n");
                graph.addObjectTriple(new Triple(randomSubject, targetEdge1, randomObject));
                graph.addObjectTriple(new Triple(randomObject, targetEdge2, targetNode));
                positives.add(randomSubject);
            }

            // let's add random triples / write negatives
            for (String node : nodeIds) {

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);
                for (int i = 0; i < tripleNumber; i++) {

                    Triple triple1 = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    Triple triple2 = generateTripleWithStartNode(triple1.object, nodeIds, edgeIds);

                    if (
                        // case: we accidentally built a positive
                            (triple1.predicate.equals(targetEdge1)
                                    && triple2.predicate.equals(targetEdge2)
                                    && triple2.object.equals(targetNode)
                                    && !positives.contains(triple1.subject))
                    ) {
                        i--;
                    } else if (
                        // case: we accidentally built a missing piece to some positive
                        // by X E1 Y whereby there exists some statement Y E2 T
                            triple1.predicate.equals(targetEdge1) &&
                                    graph.getAllObjectTriples().contains(
                                            new Triple(triple1.object, targetEdge2, targetNode)
                                    )
                    ) {
                        i--;
                    } else if (
                        // case: we accidentally built a missing piece to some positive
                        // by Y E2 T where there exists some statement Y E1 T
                            triple2.predicate.equals(targetEdge2)
                                    && triple2.object.equals(targetNode)
                                    && graph.getAllObjectTriples().contains(
                                    new Triple(triple1.subject, targetEdge1, triple1.object)
                            )
                    ) {
                        i--;
                    } else if (
                        // case: triple 1 is the missing piece to some positive
                            triple1.predicate.equals(targetEdge2)
                                    && triple1.object.equals(targetNode)
                    ) {
                        // we now need to make sure that EVERY subject in subject -> targetEdge1 -> triple1.subject
                        // is a positive match
                        // if there is one negative, we cannot add the triple!
                        for (String subject :
                                TripleDataSetMemory.getSubjectsFromTripleSet(
                                        graph.getObjectTriplesWithPredicateObject(targetEdge1, triple1.subject))
                        ) {
                            if (!positives.contains(subject)) {
                                i--;
                                continue;
                            }
                            writer.write(triple1.subject + " " + triple1.predicate + " " + triple1.object + " . \n");
                            writer.write(triple2.subject + " " + triple2.predicate + " " + triple2.object + " . \n");
                            graph.addObjectTriple(triple1);
                            graph.addObjectTriple(triple2);
                        }

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
}
