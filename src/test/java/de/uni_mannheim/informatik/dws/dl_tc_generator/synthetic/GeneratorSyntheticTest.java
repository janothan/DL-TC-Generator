package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorSyntheticTest {

    @Test
    void generateTestCases() {

        GeneratorSynthetic generator = new GeneratorSynthetic("./synthetic_tc");
        generator.generateTestCases();

    }
}