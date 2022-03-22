package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import org.junit.jupiter.api.Test;

import java.util.Set;

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

        assertEquals(0, t.getChildrenOfNode(null).size());
        assertEquals(0, t.getParentsOfNode(null).size());

        Set<String> allChildren = t.getAllChildrenOfNode("root");
        assertEquals(4, allChildren.size());
        assertTrue(allChildren.contains("a2b"));
        assertFalse(allChildren.contains("root"));

        assertEquals(0, t.getAllChildrenOfNode("a2b").size());

        allChildren = t.getAllChildrenOfNode("t1a");
        assertEquals(2, allChildren.size());
        assertTrue(allChildren.contains("a2a"));
        assertTrue(allChildren.contains("a2b"));

        Set<String> parents = t.getAllParentsOfNode("a2a");
        assertEquals(2, parents.size());
        assertTrue(parents.contains("t1a"));
        assertTrue(parents.contains("root"));

        parents = t.getAllParentsOfNode("t1b");
        assertEquals(1, parents.size());
        assertTrue(parents.contains("root"));
    }

}