package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc07GeneratorSyntheticConstructed;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Tc07GeneratorSyntheticConstructedHard extends TcGeneratorSyntheticConstructedHard {


    private static Logger LOGGER = LoggerFactory.getLogger(Tc07GeneratorSyntheticConstructedHard.class);

    public Tc07GeneratorSyntheticConstructedHard(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc07GeneratorSyntheticConstructedHard(File directory) {
        super(directory);
    }

    public Tc07GeneratorSyntheticConstructedHard(String directory) {
        super(directory);
    }

    @Override
    public String getTcId() {
        return "TC07h";
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

            // negatives involving targetEdge1, targetEdge2
            while (negatives.size() < Math.ceil(nodesOfInterest / 3.0)) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if (positives.contains(randomSubject)) {
                    continue;
                }
                final String randomObject1 = Util.randomDrawFromSet(nodeIds);
                final String randomObject2 = Util.randomDrawFromSet(nodeIds);
                if(randomObject2.equals(targetNode)){
                    continue;
                }
                Triple t1 = new Triple(randomSubject, targetEdge1, randomObject1);
                Triple t2 = new Triple(randomObject1, targetEdge2, randomObject2);
                if (!Tc07GeneratorSyntheticConstructed.isAccidentallyPositive(t1, t2, targetEdge1, targetEdge2,
                        targetNode, positives, graph)) {
                            writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                            writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                            graph.addObjectTriple(t1);
                            graph.addObjectTriple(t2);
                            negatives.add(t1.subject);
                        }
            }

            // negatives involving targetEdge1, targetNode
            while (negatives.size() < Math.ceil(nodesOfInterest / 3.0)*2) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if (positives.contains(randomSubject)) {
                    continue;
                }
                final String randomObject1 = Util.randomDrawFromSet(nodeIds);
                final String randomEdge2 = Util.randomDrawFromSet(edgeIds);
                if(randomEdge2.equals(targetEdge2)){
                    continue;
                }
                Triple t1 = new Triple(randomSubject, targetEdge1, randomObject1);
                Triple t2 = new Triple(randomObject1, randomEdge2, targetNode);
                if (!Tc07GeneratorSyntheticConstructed.isAccidentallyPositive(t1, t2, targetEdge1, targetEdge2,
                        targetNode, positives, graph)) {
                    writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                    writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                    graph.addObjectTriple(t1);
                    graph.addObjectTriple(t2);
                    negatives.add(t1.subject);
                }
            }

            // negatives involving targetEdge2, targetNode
            while (negatives.size() < Math.ceil(nodesOfInterest)) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if (positives.contains(randomSubject)) {
                    continue;
                }
                final String randomObject1 = Util.randomDrawFromSet(nodeIds);
                final String randomEdge1 = Util.randomDrawFromSet(edgeIds);
                if(randomEdge1.equals(targetEdge1)){
                    continue;
                }
                Triple t1 = new Triple(randomSubject, randomEdge1, randomObject1);
                Triple t2 = new Triple(randomObject1, targetEdge2, targetNode);
                if (!Tc07GeneratorSyntheticConstructed.isAccidentallyPositive(t1, t2, targetEdge1, targetEdge2,
                        targetNode, positives, graph)) {
                    writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                    writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                    graph.addObjectTriple(t1);
                    graph.addObjectTriple(t2);
                    negatives.add(t1.subject);
                }
            }

            // let's add random triples / write negatives
            for (String node : nodeIds) {

                // draw number of triples
                int tripleNumber = random.nextInt(getMaxTriplesPerNode() + 1);
                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple1 = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    Triple triple2 = generateTripleWithStartNode(triple1.object, nodeIds, edgeIds);

                    if (Tc07GeneratorSyntheticConstructed.isAccidentallyPositive(triple1, triple2, targetEdge1, targetEdge2, targetNode, positives, graph)){
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
