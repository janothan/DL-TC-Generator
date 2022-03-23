package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
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
 *     	?x a dbo:Person .
 *     	?y1 dbo:director ?x .
 *     	?y1 a dbo:Film .
 *     	?y2 dbo:director ?x .
 *     	?y2 a dbo:Film .
 *     }
 *     FILTER
 *     (
 *         ?y1 != ?y2
 *     )
 * } LIMIT <number>
 *}
 */
public class Tc12GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc12GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc12GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc12GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    @Override
    public String getTcId() {
        return "tc12";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc12GeneratorSyntheticOntology.class);

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        final String targetProperty = ontologyGenerator.getRandomPropertyWhereRangeAtLeastTwoSubtypes();
        final String targetClass = ontologyGenerator.getRange(targetProperty);
        final String domainPropertySuperClass = ontologyGenerator.getDomain(targetProperty);

        final Iterator<String> targetDomainIterator =
                ontologyGenerator.getClassTree().getChildrenOfNode(domainPropertySuperClass).iterator();
        final String positiveDomainClass = targetDomainIterator.next();

        ontologyGenerator.ensureEnoughInstancesOfType(targetClass, 2 * nodesOfInterest);
        ontologyGenerator.ensureEnoughInstancesOfType(positiveDomainClass, 3 * nodesOfInterest);

        final Set<String> domainInstances = ontologyGenerator.getInstancesOfTypeTransitive(positiveDomainClass);
        final Set<String> targetInstances = ontologyGenerator.getInstancesOfTypeTransitive(targetClass);
        final Iterator<String> targetInstanceIterator = targetInstances.iterator();

        writeConfigToNewLog(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
        configLog.append("Target property: ").append(targetProperty).append("\n");
        configLog.append("Target subject class: ").append(positiveDomainClass).append("\n");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            // let's generate positives
            while (positives.size() < nodesOfInterest) {
                String subject1 = Util.randomDrawFromSet(domainInstances);
                String subject2 = Util.randomDrawFromSet(domainInstances);
                if (subject1.equals(subject2)) {
                    continue;
                }
                String object = targetInstanceIterator.next();
                positives.add(object);
                graph.addObjectTriple(new Triple(subject1, targetProperty, object));
                graph.addObjectTriple(new Triple(subject2, targetProperty, object));
                writer.write(subject1 + " " + targetProperty + " " + object + " .\n");
                writer.write(subject2 + " " + targetProperty + " " + object + " .\n");
            }

            // let's generate negatives
            while (negatives.size() < nodesOfInterest) {
                String subject = Util.randomDrawFromSet(domainInstances);
                String object = targetInstanceIterator.next();
                negatives.add(object);
                graph.addObjectTriple(new Triple(subject, targetProperty, object));
                writer.write(subject + " " + targetProperty + " " + object + " .\n");
            }

            // let's generate random triples
            for (String instanceId : ontologyGenerator.getInstances()) {

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);

                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = ontologyGenerator.getRandomTripleWithSubject(instanceId);
                    if (triple.predicate.equals(targetProperty)) {
                        i--;
                    } else {
                        writer.write(triple.subject + " " + triple.predicate + " " + triple.object + " . \n");
                        graph.addObjectTriple(triple);
                    }
                }
            }

        } catch (
                IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

}
