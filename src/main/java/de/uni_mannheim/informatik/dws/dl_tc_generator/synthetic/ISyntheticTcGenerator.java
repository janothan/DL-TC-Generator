package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.TrainTestSplit;

public interface ISyntheticTcGenerator {


    /**
     * Generate the test case.
     */
    void generate();

    /**
     * Set the size groups that shall be generated.
     * @param sizes The sizes as int array.
     */
    void setSizes(int[] sizes);

    /**
     * Set the separator to be used in test/train files.
     * @param separator CSV separator.
     */
    void setSeparator(String separator);

    /**
     * Set the train-test split.
     * @param trainTestSplit The split.
     */
    void setTrainTestSplit(TrainTestSplit trainTestSplit);
}
