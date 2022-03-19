package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

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

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc06GeneratorSyntheticOntology.class);

    StringBuilder configLog = null;

    @Override
    public String getTcId() {
        return null;
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {

        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            // todo continue here

        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }
}
