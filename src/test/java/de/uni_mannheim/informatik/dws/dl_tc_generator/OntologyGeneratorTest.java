package de.uni_mannheim.informatik.dws.dl_tc_generator;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology.ConstantSplitTreeGenerator;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology.OntologyGenerator;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OntologyGeneratorTest {


    @Test
    void blindTest(){
        int numberInstances = 1500;
        OntologyGenerator og = new OntologyGenerator(20, 13,numberInstances, "test",
                new ConstantSplitTreeGenerator(3));
        assertTrue(og.getClasses().size() >= 20);
        assertTrue(og.getProperties().size() >= 13);
        assertTrue(og.getInstances().size() >= 100);
        assertEquals(og.getTcId(), "test");

        for(String instance : og.getInstances()){
            assertNotNull(og.getRandomPropertyForInstance(instance));
            Triple t = og.getRandomPropertyObjectForInstance(instance);
            assertNotNull(t);
            assertNotNull(t.subject);
            assertTrue(og.getInstances().contains(t.subject));
            assertNotNull(t.predicate);
            assertTrue(og.getProperties().contains(t.predicate));
            assertNotNull(t.object);
            assertTrue(og.getInstances().contains(t.object));

            String predicate = og.getRandomPropertyForInstance(instance);
            assertNotNull(predicate);

            String type = og.getInstanceType(instance);
            assertNotNull(type);

            String domain = og.getDomain(predicate);
            assertNotNull(domain);

            Set<String> children = new HashSet<>(og.getClassTree().getAllChildrenOfNode(domain));
            children.add(domain);

            if(!children.contains(type)){
                fail("Type '" + type + "' not found in domain set.");
            }
        }

        for(String predicateId : og.getProperties()){
            assertNotNull(og.getDomain(predicateId));
            assertNotNull(og.getRange(predicateId));
        }

        int instanceCounter = 0;
        long instanceCounterTransitive = 0;
        for(String classId : og.getClasses()){
            instanceCounter += og.getClassInstancesNonTransitive(classId).size();
            instanceCounterTransitive += og.getClassInstancesTransitive(classId).size();
        }
        assertEquals(numberInstances, instanceCounter);
        assertTrue(instanceCounterTransitive > instanceCounter);
    }

    @Test
    void ensureEnoughInstancesOfType(){
        final int instanceNumber = 30;
        final int classNumber = 3;
        OntologyGenerator og = new OntologyGenerator(classNumber, 1, instanceNumber, "test",
                new ConstantSplitTreeGenerator(1000));
        int iNumber = og.getInstances().size();
        assertEquals(iNumber, instanceNumber);

        assertEquals(classNumber, og.getClasses().size() );
        String type = og.getClasses().iterator().next();
        assertNotNull(type);

        int typedInstancesNumber = og.getInstancesOfTypeTransitive(type).size();
        assertTrue(typedInstancesNumber < 100);

        og.ensureEnoughInstancesOfType(type, 100);
        assertEquals(100, og.getInstancesOfTypeTransitive(type).size());
        int actualInstanceSize = og.getInstances().size();
        assertTrue(actualInstanceSize >= 100, // note that we may draw the root, so 100 is ok!
                "Expected > 100 instances. Was: " + actualInstanceSize);

        for (String instance : og.getInstances()){
            Triple t = og.getRandomPropertyObjectForInstance(instance);
            assertNotNull(t);
        }
    }

}