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
            assertNotNull(og.getRandomPropertyWhereInstanceIsDomain(instance));
            Triple t = og.getRandomTripleWithSubject(instance);
            assertNotNull(t);
            assertNotNull(t.subject);
            assertTrue(og.getInstances().contains(t.subject));
            assertNotNull(t.predicate);
            assertTrue(og.getProperties().contains(t.predicate));
            assertNotNull(t.object);
            assertTrue(og.getInstances().contains(t.object));

            String predicate = og.getRandomPropertyWhereInstanceIsDomain(instance);
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
        String type = Util.randomDrawFromSet(og.getClasses());
        assertNotNull(type);

        int typedInstancesNumber = og.getInstancesOfTypeTransitive(type).size();
        assertTrue(typedInstancesNumber < 100);

        og.ensureEnoughInstancesOfType(type, 100);
        assertEquals(100, og.getInstancesOfTypeTransitive(type).size());
        int actualInstanceSize = og.getInstances().size();
        assertTrue(actualInstanceSize >= 100, // note that we may draw the root, so 100 is ok!
                "Expected > 100 instances. Was: " + actualInstanceSize);

        for (String instance : og.getInstances()){
            Triple t = og.getRandomTripleWithSubject(instance);
            assertNotNull(t);
        }
    }

    /**
     * Almost identical to {@link OntologyGeneratorTest#ensureEnoughInstancesOfType()}.
     * Differences: Larger ontology, deeper class tree.
     */
    @Test
    void ensureEnoughInstancesOfType2(){
        final int instanceNumber = 1000;
        final int classNumber = 100;
        OntologyGenerator og = new OntologyGenerator(classNumber, 1, instanceNumber, "test",
                new ConstantSplitTreeGenerator(2));
        int iNumber = og.getInstances().size();
        assertEquals(iNumber, instanceNumber);

        assertEquals(classNumber, og.getClasses().size() );
        String type = Util.randomDrawFromSet(og.getClasses());
        assertNotNull(type);

        int typedInstancesNumber = og.getInstancesOfTypeTransitive(type).size();
        assertTrue(typedInstancesNumber <= 1000);

        og.ensureEnoughInstancesOfType(type, 1500);
        assertEquals(1500, og.getInstancesOfTypeTransitive(type).size());

        int actualInstanceSize = og.getInstances().size();
        assertTrue(actualInstanceSize >= 1500, // note that we may draw the root, so 100 is ok!
                "Expected > 1500 instances. Was: " + actualInstanceSize);

        // now we test whether the indices have been correctly updated.
        for (String instance : og.getInstances()){
            Triple t = og.getRandomTripleWithSubject(instance);
            assertNotNull(t);

            String property = og.getRandomPropertyWhereInstanceIsDomain(instance);
            assertNotNull(property);
            String propertyDomain = og.getDomain(property);
            Set<String> domainTypes = new HashSet<>(og.getClassTree().getAllChildrenOfNode(propertyDomain));
            domainTypes.add(propertyDomain);
            assertTrue(domainTypes.contains(og.getInstanceType(instance)));

            property = og.getRandomPropertyWhereInstanceIsRange(instance);
            assertNotNull(property);
            String propertyRange = og.getRange(property);
            Set<String> rangeTypes = new HashSet<>(og.getClassTree().getAllChildrenOfNode(propertyRange));
            rangeTypes.add(propertyRange);
            assertTrue(rangeTypes.contains(og.getInstanceType(instance)));
        }
    }

    @Test
    void getPropertyWhereDomainRangeHasMoreThanTwoSubtypes(){
        final int instanceNumber = 3000;
        final int classNumber = 10;
        OntologyGenerator og = new OntologyGenerator(classNumber, 3, instanceNumber, "test",
                new ConstantSplitTreeGenerator(2));

        String propertyDomain = og.getRandomPropertyWhereDomainHasAtLeastTwoSubtypes();
        String domain = og.getDomain(propertyDomain);
        Set<String> domainChildren = og.getClassTree().getChildrenOfNode(domain);
        assertTrue(domainChildren.size() >= 2);
        for(String c1 : domainChildren){
            for(String c2 : domainChildren){
                if(c1.equals(c2)){
                    continue;
                }
                assertFalse(og.getClassTree().isA(c1, c2));
                assertFalse(og.getClassTree().isA(c2, c1));
            }
        }

        String propertyRange = og.getRandomPropertyWhereRangeHasAtLeastTwoSubtypes();
        String range = og.getRange(propertyRange);
        Set<String> rangeChildren = og.getClassTree().getChildrenOfNode(range);
        assertTrue(rangeChildren.size() >= 2);
        for(String c1 : rangeChildren){
            for (String c2 : rangeChildren) {
                if(c1.equals(c2)){
                    continue;
                }
                assertFalse(og.getClassTree().isA(c1, c2));
                assertFalse(og.getClassTree().isA(c2, c1));
            }
        }
    }


}