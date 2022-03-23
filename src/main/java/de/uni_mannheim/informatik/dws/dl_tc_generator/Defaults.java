package de.uni_mannheim.informatik.dws.dl_tc_generator;

/**
 * Configuration Defaults.
 * DBpedia statistics are taken from:
 * <a href="https://arxiv.org/pdf/2003.00719.pdf">Heist, Nicolas; Hertling, Sven; Ringler, Daniel; Paulheim, Heiko. Knowledge
 * Graphs on the Web - an Overview. 2020.</a>
 */
public class Defaults {


    public static final TrainTestSplit TRAIN_TEST_SPLIT = new TrainTestSplit(0.2, 0.8);

    /**
     * Separator for test/train files.
     */
    public static final String CSV_SEPARATOR = "\t";

    /**
     * The default size classes.
     */
    public static final int[] SIZES = {50, 500, 5000};

    /**
     * DBpedia: AVG linking degree 21 -> 11 max triples (11 ingoing + 11 outgoing)
     */
    public static final int MAX_TRIPLES_PER_NODE = 11;

    /**
     * The maximum number of edges a synthetic graph may have.
     * DBpedia: 1355
     */
    public static final int NUMBER_OF_EDGES = 1355;

    /**
     * The number of classes that is desired (different from {@link Defaults#SIZES}!).
     * DBpedia: 760
     */
    public static final int NUMBER_OF_CLASSES = 760;

    /**
     * Default number of sub-classes per class.
     * DBpedia: 4.53
     */
    public static final int BRANCHING_FACTOR = 5;

    /**
     * The number of node types (nodes_of_interest * NODE_FACTOR)
     */
    public static final int NODE_FACTOR = 15;
}
