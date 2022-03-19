package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
        // todo continue here

    }
}
