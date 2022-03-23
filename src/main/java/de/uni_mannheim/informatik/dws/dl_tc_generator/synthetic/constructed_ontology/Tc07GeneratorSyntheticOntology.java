package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc05GeneratorSyntheticConstructed;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * Positive Query
 * {@code
 * SELECT DISTINCT ?x WHERE
 * {
 *     {
 *         ?x a dbo:Person .
 *         ?x dbo:team ?y .
 *         ?y a dbo:BasketballTeam .
 *     }
 * }
 * }
 */
public class Tc07GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc07GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc07GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc07GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    public Tc07GeneratorSyntheticOntology(File directory, int numberOfClasses, int numberOfEdges,
                                          int totalNodesFactor, int maxTriplesPerNode, int branchingFactor,
                                          int[] sizes) {
        super(directory, numberOfClasses, numberOfEdges, totalNodesFactor, maxTriplesPerNode, branchingFactor, sizes);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc05GeneratorSyntheticConstructed.class);

    @Override
    public String getTcId() {
        return "tc07";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            final String targetProperty = ontologyGenerator.getRandomPropertyWhereRangeHasAtLeastTwoSubtypes();

            final String targetClass = ontologyGenerator.getDomain(targetProperty);
            ontologyGenerator.ensureEnoughInstancesOfType(targetClass, 2 * nodesOfInterest);
            Set<String> targetSubjectInstances = ontologyGenerator.getInstancesOfTypeTransitive(targetClass);

            Iterator<String> rangeTypeIterator =
                    ontologyGenerator.classTree.getChildrenOfNode(ontologyGenerator.getRange(targetProperty)).iterator();

            String positiveUpperObjectType = rangeTypeIterator.next();
            String negativeUpperObjectType = rangeTypeIterator.next();

            ontologyGenerator.ensureEnoughInstancesOfType(positiveUpperObjectType, nodesOfInterest);
            ontologyGenerator.ensureEnoughInstancesOfType(negativeUpperObjectType, nodesOfInterest);

            Iterator<String> subjectIterator = targetSubjectInstances.iterator();

            writeConfigToNewLog(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
            configLog.append("Target property: ").append(targetProperty).append("\n");
            configLog.append("Positive object class: ").append(positiveUpperObjectType).append("\n");
            configLog.append("Negative object class: ").append(negativeUpperObjectType).append("\n");

            // let's generate positives
            while (positives.size() < nodesOfInterest) {
                String subject = subjectIterator.next();
                positives.add(subject);
                String object = ontologyGenerator.getRandomInstanceOfTypeTransitive(positiveUpperObjectType);
                graph.addObjectTriple(new Triple(subject, targetProperty, object));
                writer.write(subject + " " + targetProperty + " " + object + " .\n");
            }

            // let's generate negatives
            while (negatives.size() < nodesOfInterest) {
                String subject = subjectIterator.next();
                negatives.add(subject);
                String object = ontologyGenerator.getRandomInstanceOfTypeTransitive(negativeUpperObjectType);
                graph.addObjectTriple(new Triple(subject, targetProperty, object));
                writer.write(subject + " " + targetProperty + " " + object + " .\n");
            }

            // let's generate random connections
            for (String instanceId : ontologyGenerator.getInstances()) {

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);

                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = ontologyGenerator.getRandomTripleWithSubject(instanceId);
                    if ( triple.predicate.equals(targetProperty) ) {
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
