package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import java.io.File;

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
        return null;
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {

    }
}
