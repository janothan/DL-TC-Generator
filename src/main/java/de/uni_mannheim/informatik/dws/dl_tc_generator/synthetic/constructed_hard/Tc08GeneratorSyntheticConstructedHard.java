package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc08GeneratorSyntheticConstructed.isAccidentallyPositive;

/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: Z<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (Y E1 X) AND (Y E2 Z) <br/>
 * }
 */
public class Tc08GeneratorSyntheticConstructedHard extends TcGeneratorSyntheticConstructedHard {


    public Tc08GeneratorSyntheticConstructedHard(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc08GeneratorSyntheticConstructedHard(File directory) {
        super(directory);
    }

    public Tc08GeneratorSyntheticConstructedHard(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc08GeneratorSyntheticConstructedHard.class);


    @Override
    public String getTcId() {
        return "TC8h";
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
            while (positives.size() < nodesOfInterest) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                final String positive = Util.randomDrawFromSet(nodeIds);
                writer.write(randomSubject + " " + targetEdge1 + " " + positive + " . \n");
                writer.write(randomSubject + " " + targetEdge2 + " " + targetNode + " . \n");
                graph.addObjectTriple(new Triple(randomSubject, targetEdge1, positive));
                graph.addObjectTriple(new Triple(randomSubject, targetEdge2, targetNode));
                positives.add(positive);
            }

            // negatives involving targetEdge1 and targetEdge2
            while(negatives.size() < Math.ceil(nodesOfInterest/3.0)) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                final String randomObject1 = Util.randomDrawFromSet(nodeIds);
                final String randomObject2 = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(randomObject1)){
                    continue;
                }
                Triple t1 = new Triple(randomSubject, targetEdge1, randomObject1);
                Triple t2 = new Triple(randomSubject, targetEdge2, randomObject2);
                if(!isAccidentallyPositive(t1, t2, targetEdge1, targetEdge2, targetNode, positives, graph)){
                    graph.addObjectTriple(t1);
                    graph.addObjectTriple(t2);
                    writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                    writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                    negatives.add(randomObject1);
                }
            }

            // negatives involving targetEdge2 and targetNode
            while(negatives.size() < Math.ceil(nodesOfInterest/3.0)*2) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                final String randomObject1 = Util.randomDrawFromSet(nodeIds);
                final String randomEdge1 = Util.randomDrawFromSet(edgeIds);
                if(positives.contains(randomObject1)){
                    continue;
                }
                if(randomEdge1.equals(targetEdge1)){
                    continue;
                }
                Triple t1 = new Triple(randomSubject, randomEdge1, randomObject1);
                Triple t2 = new Triple(randomSubject, targetEdge2, targetNode);
                if(!isAccidentallyPositive(t1, t2, targetEdge1, targetEdge2, targetNode, positives, graph)){
                    graph.addObjectTriple(t1);
                    graph.addObjectTriple(t2);
                    writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                    writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                    negatives.add(randomObject1);
                }
            }

            // negatives involving targetEdge1 and targetNode
            while(negatives.size() < Math.ceil(nodesOfInterest)) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                final String randomObject1 = Util.randomDrawFromSet(nodeIds);
                final String randomEdge2 = Util.randomDrawFromSet(edgeIds);
                if(positives.contains(randomObject1)){
                    continue;
                }
                if(randomEdge2.equals(targetEdge2)){
                    continue;
                }
                Triple t1 = new Triple(randomSubject, targetEdge1, randomObject1);
                Triple t2 = new Triple(randomSubject, randomEdge2, targetNode);
                if(!isAccidentallyPositive(t1, t2, targetEdge1, targetEdge2, targetNode, positives, graph)){
                    graph.addObjectTriple(t1);
                    graph.addObjectTriple(t2);
                    writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                    writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                    negatives.add(randomObject1);
                }
            }

            // let's add random triples / write negatives
            for (String node : nodeIds) {

                // draw number of triples
                int tripleNumber = random.nextInt(getMaxTriplesPerNode() + 1);
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

}
