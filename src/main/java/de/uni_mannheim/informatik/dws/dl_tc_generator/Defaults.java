package de.uni_mannheim.informatik.dws.dl_tc_generator;

/**
 * Configuration Defaults.
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
     * The maximum number of edges a synthetic graph may have.
     */
    public static final int NUMBER_OF_EDGES = 50;

    /**
     * The number of classes that is desired (different from {@link Defaults#SIZES}!).
     */
    public static final int NUMBER_OF_CLASSES = 20;

    /**
     * Default number of sub-classes per class.
     */
    public static final int CLASS_SPLITS = 3;

    /**
     * The number of node types (nodes_of_interest * NODE_FACTOR)
     */
    public static final int NODE_FACTOR = 10;
}
