package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class Tc06GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc06GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc06GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc06GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    public Tc06GeneratorSyntheticOntology(File directory, int numberOfClasses, int numberOfEdges,
                                          int totalNodesFactor, int maxTriplesPerNode, int branchingFactor,
                                          int[] sizes) {
        super(directory, numberOfClasses, numberOfEdges, totalNodesFactor, maxTriplesPerNode, branchingFactor, sizes);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc06GeneratorSyntheticOntology.class);

    @Override
    public String getTcId() {
        return "tc06";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {

        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            final String targetEdge = ontologyGenerator.getRandomPropertyId();

            // making sure that we have enough positives
            ontologyGenerator.ensureSubjectNumberForProperty(targetEdge, nodesOfInterest);

            // making sure that we have enough negatives
            final String desiredType = ontologyGenerator.getDomain(targetEdge);
            ontologyGenerator.ensureEnoughInstancesOfType(desiredType, 2 * nodesOfInterest);

            final String targetInstance = ontologyGenerator.getRandomObjectForProperty(targetEdge);

            writeConfigToNewLog(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
            configLog.append("Target class: ").append(desiredType).append("\n");
            configLog.append("Target edge: ").append(targetEdge).append("\n");
            configLog.append("Target instance: ").append(targetInstance).append("\n");

            Set<String> typeInstances = ontologyGenerator.getInstancesOfTypeTransitive(desiredType);
            Iterator<String> typeInstanceIterator = typeInstances.iterator();

            // let's generate positives
            LOGGER.info("Generating positives.");
            while (positives.size() < nodesOfInterest) {
                String positive = typeInstanceIterator.next();
                writer.write(positive + " " + targetEdge + " " + targetInstance + " .\n");
                graph.addObjectTriple(positive, targetEdge, targetInstance);
                positives.add(positive);
            }

            // make sure that we can generate hard negatives!
            if (ontologyGenerator.getPropertyRangeInstances(targetEdge).size() == 1) {
                ontologyGenerator.ensureEnoughInstancesOfType(
                        ontologyGenerator.getRange(targetEdge),
                        nodesOfInterest);
            }

            typeInstances.removeAll(positives);
            typeInstanceIterator = typeInstances.iterator();

            // let's generate hard negatives
            LOGGER.info("Generating negatives.");
            while (negatives.size() < nodesOfInterest) {
                String negative = typeInstanceIterator.next();
                if (positives.contains(negative) || negatives.contains(negative)) {
                    continue;
                }
                String someObject = ontologyGenerator.getRandomObjectForProperty(targetEdge);
                if (someObject.equals(targetInstance)) {
                    continue;
                }
                writer.write(negative + " " + targetEdge + " " + someObject + " .\n");
                graph.addObjectTriple(negative, targetEdge, someObject);
                negatives.add(negative);
            }

            // let's generate random connections
            LOGGER.info("Generating random connections.");
            for (String instanceId : ontologyGenerator.getInstances()) {

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);

                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = ontologyGenerator.getRandomTripleWithSubject(instanceId);
                    if (triple.predicate.equals(targetEdge) && triple.object.equals(targetInstance)) {
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
