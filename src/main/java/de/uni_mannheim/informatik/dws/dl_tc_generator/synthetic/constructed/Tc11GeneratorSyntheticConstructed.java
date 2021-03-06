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
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: N<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (X E1 Y1) AND (X E1 Y2) AND (Y1 E2 N) AND (Y2 E2 N)  <br/>
 * }
 */
public class Tc11GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc11GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc11GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc11GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc11GeneratorSyntheticConstructed.class);

    @Override
    public String getTcId() {
        return "TC11";
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

            // generating negatives randomly
            for(String node : nodeIds) {

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

        if(triple.predicate.equals(targetEdge2) && triple.object.equals(targetNode)){
            // we have Y E2 N -> ensure that the pattern does not match

            Set<String> potentialPositives =
                    TripleDataSetMemory.getSubjectsFromTripleSet(
                            graph.getObjectTriplesWithPredicateObject(targetEdge1, triple.subject)
                    );

            if(potentialPositives != null){

                for(String s : potentialPositives){
                    Set<String> e1objects = TripleDataSetMemory.getSubjectsFromTripleSet(
                            graph.getObjectTriplesWithSubjectPredicate(s, targetEdge1)
                    );

                    if(e1objects != null){

                        for(String y : e1objects) {
                            Set<String> potentialN = TripleDataSetMemory.getObjectsFromTripleSet(
                                    graph.getObjectTriplesWithSubjectPredicate(y, targetEdge2)
                            );

                            if(potentialN != null){
                                for (String n : potentialN){
                                    if (n.equals(targetNode)) {
                                       return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (triple.predicate.equals(targetEdge1)){

            if (graph.getAllObjectTriples().contains(new Triple(triple.object, targetEdge2, targetNode))) {
                // we have X E1 Y and Y E2 N is already exists in the graph
                // we need to make sure that there is no other X E1 Y2 E2 N

                Set<String> potentialY = TripleDataSetMemory.getObjectsFromTripleSet(
                        graph.getObjectTriplesWithSubjectPredicate(triple.subject, targetEdge1)
                );

                if (potentialY != null) {
                    for (String y : potentialY) {
                        List<Triple> checkTriples = graph.getObjectTriplesInvolvingSubject(y);
                        if (checkTriples != null) {
                            for (Triple checkTriple : checkTriples) {
                                if (checkTriple.predicate.equals(targetEdge2) && checkTriple.object.equals(targetNode)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

        }

        return false;
    }



}
