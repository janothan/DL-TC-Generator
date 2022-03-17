package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConstantSplitTreeGeneratorTest {


    @Test
    void generateTree() {

        Set<String> nodes = new HashSet<>(Arrays.asList("ROOT", "N1", "N2", "N3", "N4", "N5", "N6", "N7"));
        ConstantSplitTreeGenerator generator = new ConstantSplitTreeGenerator(2, "ROOT", nodes);
        Tree t = generator.generateTree();
        System.out.println(t);

        assertEquals(8, t.getAllNodes().size());

        // check parents
        for(String node : nodes){
            if(node.equals("ROOT")){
                continue;
            }
            assertEquals(1, t.getParentsOfNode(node).size());
        }

        int zeroChildCounter = 0;
        int oneChildCounter = 0;
        // check children
        for(String node : nodes){
            Set<String> children = t.getChildrenOfNode(node);
            if(children.size() == 1){
                oneChildCounter++;
            }
            if(children.size() == 0){
                zeroChildCounter++;
            }
            assertTrue(children.size() <= 2);
        }
        assertEquals(1, oneChildCounter);
        assertEquals(4, zeroChildCounter);

    }
}