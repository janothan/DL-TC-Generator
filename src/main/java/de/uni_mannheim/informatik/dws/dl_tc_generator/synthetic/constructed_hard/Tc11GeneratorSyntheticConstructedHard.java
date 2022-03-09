package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;


import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc11GeneratorSyntheticConstructed.isAccidentallyPositive;

public class Tc11GeneratorSyntheticConstructedHard extends TcGeneratorSyntheticConstructedHard {


    public Tc11GeneratorSyntheticConstructedHard(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc11GeneratorSyntheticConstructedHard(File directory) {
        super(directory);
    }

    public Tc11GeneratorSyntheticConstructedHard(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc11GeneratorSyntheticConstructedHard.class);


    @Override
    public String getTcId() {
        return "TC11h";
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
                if(randomNode1.equals(randomNode2) || randomNode1.equals(positive) || randomNode2.equals(positive)){
                    // just redraw
                    continue;
                }

                writer.write(positive + " " + targetEdge1 + " " + randomNode1 + " . \n");
                writer.write(randomNode1 + " " + targetEdge2 + " " + targetNode + " . \n");
                writer.write(positive + " " + targetEdge1 + " " + randomNode2 + " . \n");
                writer.write(randomNode2 + " " + targetEdge2 + " " + targetNode + " . \n");

                graph.addObjectTriple(new Triple(positive, targetEdge1, randomNode1));
                graph.addObjectTriple(new Triple(randomNode1, targetEdge2, targetNode));
                graph.addObjectTriple(new Triple(positive, targetEdge1, randomNode2));
                graph.addObjectTriple(new Triple(randomNode2, targetEdge2, targetNode));
                positives.add(positive);
            }

            // generate hard negatives
            long infiniteLoopIndicatorLimit = nodesOfInterest * 10L;
            long currentRun = 0;

            // involving X E1 Y1 E2 N AND X E1 Y2 E2 NOT_N
            while(negatives.size() < nodesOfInterest/3.0){
                currentRun++;

                if(currentRun == infiniteLoopIndicatorLimit){
                    LOGGER.error("Likely infinite loop for negatives involving X E1 Y1 E2 N AND X E1 Y2 E2 NOT_N");
                    break;
                }

                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(randomSubject)){
                    continue;
                }
                final String randomNotN = Util.randomDrawFromSet(nodeIds);
                if(randomNotN.equals(targetNode)){
                    continue;
                }
                final String randomY1 = Util.randomDrawFromSet(nodeIds);
                final String randomY2 = Util.randomDrawFromSet(nodeIds);

                Triple t1 = new Triple(randomSubject, targetEdge1, randomY1);
                if(isAccidentallyPositive(t1, targetEdge1, targetEdge2, targetNode, graph)){
                    continue;
                }
                graph.addObjectTriple(t1);

                Triple t2 = new Triple(randomSubject, targetEdge1, randomY2);
                if(isAccidentallyPositive(t2, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1
                    graph.removeObjectTriple(t1);
                    continue;
                }
                graph.addObjectTriple(t2);

                Triple t3 = new Triple(randomY1, targetEdge2, targetNode);
                if(isAccidentallyPositive(t3, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1, t2
                    graph.removeObjectTriple(t1);
                    graph.removeObjectTriple(t2);
                    continue;
                }
                graph.addObjectTriple(t3);

                Triple t4 = new Triple(randomY2, targetEdge2, randomNotN);
                if(isAccidentallyPositive(t4, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1, t2, t3
                    graph.removeObjectTriple(t1);
                    graph.removeObjectTriple(t2);
                    graph.removeObjectTriple(t3);
                    continue;
                }

                graph.addObjectTriple(t4);
                writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                writer.write(t3.subject + " " + t3.predicate + " " + t3.object + " . \n");
                writer.write(t4.subject + " " + t4.predicate + " " + t4.object + " . \n");
                negatives.add(randomSubject);
            }

            // involving X E1 Y1 E2 N AND X NOT_E1 Y2 E2 N
            currentRun = 0;
            while(negatives.size() < (nodesOfInterest/3.0)*2){
                currentRun++;

                if(currentRun == infiniteLoopIndicatorLimit){
                    LOGGER.error("Likely infinite loop for negatives involving X E1 Y1 E2 N AND X NOT_E1 Y2 E2 N");
                    break;
                }
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(randomSubject)){
                    continue;
                }
                final String randomNotE1 = Util.randomDrawFromSet(edgeIds);
                if(randomNotE1.equals(targetEdge1)){
                    continue;
                }
                final String randomY1 = Util.randomDrawFromSet(nodeIds);
                final String randomY2 = Util.randomDrawFromSet(nodeIds);

                Triple t1 = new Triple(randomSubject, targetEdge1, randomY1);
                if(isAccidentallyPositive(t1, targetEdge1, targetEdge2, targetNode, graph)){
                    continue;
                }
                graph.addObjectTriple(t1);

                Triple t2 = new Triple(randomSubject, randomNotE1, randomY2);
                if(isAccidentallyPositive(t2, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1
                    graph.removeObjectTriple(t1);
                    continue;
                }
                graph.addObjectTriple(t2);

                Triple t3 = new Triple(randomY1, targetEdge2, targetNode);
                if(isAccidentallyPositive(t3, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1, t2
                    graph.removeObjectTriple(t1);
                    graph.removeObjectTriple(t2);
                    continue;
                }
                graph.addObjectTriple(t3);

                Triple t4 = new Triple(randomY2, targetEdge2, targetNode);
                if(isAccidentallyPositive(t4, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1, t2, t3
                    graph.removeObjectTriple(t1);
                    graph.removeObjectTriple(t2);
                    graph.removeObjectTriple(t3);
                    continue;
                }

                graph.addObjectTriple(t4);
                writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                writer.write(t3.subject + " " + t3.predicate + " " + t3.object + " . \n");
                writer.write(t4.subject + " " + t4.predicate + " " + t4.object + " . \n");
                negatives.add(randomSubject);
            }

            currentRun = 0;
            // involving X E1 Y1 E2 N AND X E1 Y2 NOT_E2 N
            while(negatives.size() < nodesOfInterest){
                currentRun++;

                if(currentRun == infiniteLoopIndicatorLimit){
                    LOGGER.error("Likely infinite loop for negatives involving X E1 Y1 E2 N AND X E1 Y2 NOT_E2 N");
                    break;
                }
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(randomSubject)){
                    continue;
                }
                final String randomNotE2 = Util.randomDrawFromSet(edgeIds);
                if(randomNotE2.equals(targetEdge2)){
                    continue;
                }
                final String randomY1 = Util.randomDrawFromSet(nodeIds);
                final String randomY2 = Util.randomDrawFromSet(nodeIds);

                Triple t1 = new Triple(randomSubject, targetEdge1, randomY1);
                if(isAccidentallyPositive(t1, targetEdge1, targetEdge2, targetNode, graph)){
                    continue;
                }
                graph.addObjectTriple(t1);

                Triple t2 = new Triple(randomSubject, targetEdge1, randomY2);
                if(isAccidentallyPositive(t2, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1
                    graph.removeObjectTriple(t1);
                    continue;
                }
                graph.addObjectTriple(t2);

                Triple t3 = new Triple(randomY1, targetEdge2, targetNode);
                if(isAccidentallyPositive(t3, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1, t2
                    graph.removeObjectTriple(t1);
                    graph.removeObjectTriple(t2);
                    continue;
                }
                graph.addObjectTriple(t3);

                Triple t4 = new Triple(randomY2, randomNotE2, targetNode);
                if(isAccidentallyPositive(t4, targetEdge1, targetEdge2, targetNode, graph)){
                    // rollback: remove t1, t2, t3
                    graph.removeObjectTriple(t1);
                    graph.removeObjectTriple(t2);
                    graph.removeObjectTriple(t3);
                    continue;
                }
                graph.addObjectTriple(t4);

                // we are all clear: write to file
                writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " . \n");
                writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " . \n");
                writer.write(t3.subject + " " + t3.predicate + " " + t3.object + " . \n");
                writer.write(t4.subject + " " + t4.predicate + " " + t4.object + " . \n");

                negatives.add(randomSubject);
            }

            // generating negatives randomly
            for(String node : nodeIds) {

                // draw number of triples
                int tripleNumber = random.nextInt(getMaxTriplesPerNode() + 1);
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
}
