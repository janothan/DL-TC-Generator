package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import java.io.File;

public class Tc01GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc01GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc01GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc01GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {

    }

    @Override
    public String getTcId() {
        return null;
    }
}
