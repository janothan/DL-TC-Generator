package de.uni_mannheim.informatik.dws.dl_tc_generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultValidatorTest {

    @Test
    void validate() {
        ResultValidator validator = new ResultValidator("./results/dbpedia");
        assertTrue(validator.validate());
    }
}