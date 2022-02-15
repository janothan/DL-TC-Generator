package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;

import java.io.File;
import java.util.Random;

public abstract class TcGeneratorSyntheticConstructed extends TcGeneratorSynthetic {


    public TcGeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public TcGeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public TcGeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    public static final int DEFAULT_MAX_TRIPLES_PER_NODE = 3;

    int maxTriplesPerNode = DEFAULT_MAX_TRIPLES_PER_NODE;

    final Random random = new Random();

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {
        writeGraphAndSetPositives(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
    }

    protected abstract void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest,
                                                      int totalEdges, int avgTriplesPerNode);

    public int getMaxTriplesPerNode() {
        return maxTriplesPerNode;
    }

    public void setMaxTriplesPerNode(int maxTriplesPerNode) {
        this.maxTriplesPerNode = maxTriplesPerNode;
    }
}
