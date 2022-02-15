package de.uni_mannheim.informatik.dws.dl_tc_generator;

import java.util.Set;

/**
 * Interface for classes capable of generating test cases in some way.
 */
public interface IGenerator {

    /**
     * Method which generates the test cases.
     */
    void generateTestCases();

    /**
     * Method to set the size groups which shall be generated.
     * @param sizes Desired size array.
     */
    void setSizes(int[] sizes);

    /**
     * If set, the generation method ({@link IGenerator#generateTestCases()}) will only consider test case
     * collections (i.e., directories) with that name - for example, if there are tc01, tc02, ... and the set contains
     * tc01 and tc02, then only the latter two will be generated.
     * @param includeOnlyCollection The collection that shall be included.
     */
    void setIncludeOnlyCollection(String... includeOnlyCollection);
}
