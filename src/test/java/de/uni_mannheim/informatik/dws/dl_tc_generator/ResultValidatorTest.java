package de.uni_mannheim.informatik.dws.dl_tc_generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultValidatorTest {

    @Test
    void validateDBpediaTcs() {
        ResultValidator validator = new ResultValidator("./results/dbpedia");
        assertTrue(validator.validate());
    }

    @Test
    void validateSyntheticConstructedTcs() {
        ResultValidator validator = new ResultValidator("./results/synthetic_constructed");
        assertTrue(validator.validate());
    }
}