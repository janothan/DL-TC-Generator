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

}
