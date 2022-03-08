package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc12GeneratorSyntheticConstructed;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc12GeneratorSyntheticConstructed.isAccidentallyPositive;

/**
 * TC 12 Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: N<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (Y1 E1 X) AND (Y1 E2 N) AND (Y2 E1 X) AND (Y2 E2 N)  <br/>
 * }
 */
public class Tc12GeneratorSyntheticConstructedHard extends TcGeneratorSyntheticConstructedHard {


    public Tc12GeneratorSyntheticConstructedHard(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc12GeneratorSyntheticConstructedHard(File directory) {
        super(directory);
    }

    public Tc12GeneratorSyntheticConstructedHard(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc12GeneratorSyntheticConstructedHard.class);

    @Override
    public String getTcId() {
        return "TC12h";
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

            // generate negatives
            // (Y1 E1 X) AND (Y1 E2 !N) AND (Y2 E1 X) AND (Y2 E2 N)
            while (negatives.size() < nodesOfInterest / 3.0) {
                final String randomX = Util.randomDrawFromSet(nodeIds);
                if (positives.contains(randomX)) {
                    continue;
                }
                final String randomNotN = Util.randomDrawFromSet(nodeIds);
                if(randomNotN.equals(targetNode)){
                    continue;
                }
                final String randomY1 = Util.randomDrawFromSet(nodeIds);
                final String randomY2 = Util.randomDrawFromSet(nodeIds);

                Triple t1 = new Triple(randomY1, targetEdge2, randomNotN);
                if (isAccidentallyPositive(t1, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                graph.addObjectTriple(t1);

                Triple t2 = new Triple(randomY2, targetEdge2, targetNode);
                if (isAccidentallyPositive(t2, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                graph.addObjectTriple(t2);

                Triple t3 = new Triple(randomY1, targetEdge1, randomX);
                if (isAccidentallyPositive(t3, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t3.subject + " " + t3.predicate + " " + t3.object + " . \n");
                graph.addObjectTriple(t3);

                Triple t4 = new Triple(randomY2, targetEdge1, randomX);
                if (isAccidentallyPositive(t4, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t4.subject + " " + t4.predicate + " " + t4.object + " . \n");
                graph.addObjectTriple(t4);

                negatives.add(randomX);
            }

            // generate negatives
            // (Y1 E1 X) AND (Y1 !E2 N) AND (Y2 E1 X) AND (Y2 E2 N)
            while (negatives.size() < (nodesOfInterest / 3.0)*2) {
                final String randomX = Util.randomDrawFromSet(nodeIds);
                if (positives.contains(randomX)) {
                    continue;
                }
                final String randomNotE2 = Util.randomDrawFromSet(edgeIds);
                if(randomNotE2.equals(targetEdge2)){
                    continue;
                }

                final String randomY1 = Util.randomDrawFromSet(nodeIds);
                final String randomY2 = Util.randomDrawFromSet(nodeIds);


                Triple t1 = new Triple(randomY1, randomNotE2, targetNode);
                if (isAccidentallyPositive(t1, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                graph.addObjectTriple(t1);

                Triple t2 = new Triple(randomY2, targetEdge2, targetNode);
                if (isAccidentallyPositive(t2, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                graph.addObjectTriple(t2);

                Triple t3 = new Triple(randomY1, targetEdge1, randomX);
                if (isAccidentallyPositive(t3, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t3.subject + " " + t3.predicate + " " + t3.object + " . \n");
                graph.addObjectTriple(t3);

                Triple t4 = new Triple(randomY2, targetEdge1, randomX);
                if (isAccidentallyPositive(t4, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t4.subject + " " + t4.predicate + " " + t4.object + " . \n");
                graph.addObjectTriple(t4);

                negatives.add(randomX);
            }

            // generate negatives
            // (Y1 !E1 X) AND (Y1 E2 N) AND (Y2 E1 X) AND (Y2 E2 N)
            while (negatives.size() < nodesOfInterest) {
                final String randomX = Util.randomDrawFromSet(nodeIds);
                if (positives.contains(randomX)) {
                    continue;
                }
                final String randomNotE1 = Util.randomDrawFromSet(edgeIds);
                if(randomNotE1.equals(targetEdge1)){
                    continue;
                }

                final String randomY1 = Util.randomDrawFromSet(nodeIds);
                final String randomY2 = Util.randomDrawFromSet(nodeIds);


                Triple t1 = new Triple(randomY1, targetEdge2, targetNode);
                if (isAccidentallyPositive(t1, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                graph.addObjectTriple(t1);

                Triple t2 = new Triple(randomY2, targetEdge2, targetNode);
                if (isAccidentallyPositive(t2, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                graph.addObjectTriple(t2);

                Triple t3 = new Triple(randomY1, randomNotE1, randomX);
                if (isAccidentallyPositive(t3, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t3.subject + " " + t3.predicate + " " + t3.object + " . \n");
                graph.addObjectTriple(t3);

                Triple t4 = new Triple(randomY2, targetEdge1, randomX);
                if (isAccidentallyPositive(t4, targetEdge1, targetEdge2, targetNode, graph)) {
                    continue;
                }
                writer.write(t4.subject + " " + t4.predicate + " " + t4.object + " . \n");
                graph.addObjectTriple(t4);

                negatives.add(randomX);
            }

            // generating random nodes
            for (String node : nodeIds) {
                // draw number of triples
                int tripleNumber = random.nextInt(getMaxTriplesPerNode() + 1);
                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = generateTripleWithStartNode(node, nodeIds, edgeIds);

                    if (isAccidentallyPositive(triple, targetEdge1, targetEdge2, targetNode, graph)) {
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
}
