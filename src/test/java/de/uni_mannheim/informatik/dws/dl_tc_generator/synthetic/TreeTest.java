package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeTest {


    @Test
    void myTreeTest(){
        Tree t = new Tree("root");
        t.addLeaf("root", "t1a");
        t.addLeaf("root", "t1b");
        t.addLeaf("t1a", "a2a");
        t.addLeaf("t1a", "a2b");

        assertEquals(5, t.getAllNodes().size());
        assertTrue(t.getAllNodes().contains("root"));
        assertTrue(t.getAllNodes().contains("a2b"));

        assertEquals(2, t.getChildrenOfNode("root").size());
        assertTrue(t.getChildrenOfNode("root").contains("t1a"));

        assertEquals("t1a", t.getParentsOfNode("a2b").iterator().next());
    }

}