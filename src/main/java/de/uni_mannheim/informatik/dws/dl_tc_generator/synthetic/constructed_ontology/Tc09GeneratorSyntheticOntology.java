package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
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
 *         ?x dbo:award ?y1 .
 *         ?x dbo:award ?y2 .
 *     }
 *     FILTER
 *     (
 *         ?y1 != ?y2
 *     )
 * } LIMIT <number>
 * }
 */
public class Tc09GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc09GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc09GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc09GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    @Override
    public String getTcId() {
        return "tc09";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc05GeneratorSyntheticConstructed.class);

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            final String targetProperty = ontologyGenerator.getRandomPropertyId();
            final String targetClass = ontologyGenerator.getDomain(targetProperty);
            final String rangePropertyClass = ontologyGenerator.getRange(targetProperty);

            ontologyGenerator.ensureEnoughInstancesOfType(targetClass, 2*nodesOfInterest);
            ontologyGenerator.ensureEnoughInstancesOfType(rangePropertyClass, 3*nodesOfInterest);

            final Set<String> targetInstances = ontologyGenerator.getInstancesOfTypeTransitive(targetClass);
            final Set<String> rangeInstances = ontologyGenerator.getInstancesOfTypeTransitive(rangePropertyClass);
            final Iterator<String> targetInstanceIterator = targetInstances.iterator();

            writeConfigToNewLog(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
            configLog.append("Target property: ").append(targetProperty).append("\n");

            // let's generate positives
            while(positives.size() < nodesOfInterest){
                String object1 = Util.randomDrawFromSet(rangeInstances);
                String object2 = Util.randomDrawFromSet(rangeInstances);
                if(object1.equals(object2)){
                    continue;
                }
                String subject = targetInstanceIterator.next();
                positives.add(subject);
                graph.addObjectTriple(new Triple(subject, targetProperty, object1));
                graph.addObjectTriple(new Triple(subject, targetProperty, object2));
                writer.write(subject + " " + targetProperty + " " + object1 + " .\n");
                writer.write(subject + " " + targetProperty + " " + object2 + " .\n");
            }

            // let's generate negatives
            while(negatives.size() < nodesOfInterest){
                String object = Util.randomDrawFromSet(rangeInstances);
                String subject = targetInstanceIterator.next();
                negatives.add(subject);
                graph.addObjectTriple(new Triple(subject, targetProperty, object));
                writer.write(subject + " " + targetProperty + " " + object + " .\n");
            }

            // let's generate random triples
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
