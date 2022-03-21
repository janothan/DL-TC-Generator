package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OntologyGenerator implements IOntologyGenerator {


    public OntologyGenerator(int numberOfClasses, int numberOfProperties, int numberOfInstances, String tcId,
                             ITreeGenerator treeGenerator, double domainRangeP, boolean isWeaklyTyped) {
        this.tcId = tcId;
        this.domainRangeP = domainRangeP;
        this.isWeaklyTyped = isWeaklyTyped;

        // generate ids
        classIds = new HashSet<>(generateIds(numberOfClasses, "C_" + tcId));
        propertyIds = new HashSet<>(generateIds(numberOfProperties, "P_" + tcId));
        instanceIds = new HashSet<>(generateIds(numberOfInstances, "I_" + tcId));

        // build class tree
        this.treeGenerator = treeGenerator;
        classTree = treeGenerator.generateTree(classIds, classIds.iterator().next());

        // type instances
        instanceTypes = new HashMap<>();
        classInstancesNonTransitive = new HashMap<>();
        for (String instance : instanceIds) {
            String classId = Util.randomDrawFromSet(classIds);
            addInstance(instance, classId);
        }

        // build property domain and range
        propertyRanges = new HashMap<>();
        propertyRangeInstances = new HashMap<>();
        propertyDomains = new HashMap<>();
        propertyDomainInstances = new HashMap<>();
        instanceSubjectProperties = new HashMap<>();

        boolean isFirstProperty = true;
        for (String propertyId : propertyIds) {
            if (isFirstProperty) {
                addPropertyDomain(propertyId, classTree.getRoot());
                addPropertyRange(propertyId, classTree.getRoot());
                isFirstProperty = false;
            } else {
                String randomDomain, randomRange;
                if (domainRangeP >= 1.0 || domainRangeP < 0.0) {
                    LOGGER.info("Using random domain/range drawing.");
                    randomDomain = Util.randomDrawFromSet(classIds);
                    randomRange = Util.randomDrawFromSet(classIds);
                } else {
                    randomDomain = pickDomainRangeSkewed(domainRangeP);
                    randomRange = pickDomainRangeSkewed(domainRangeP);
                }
                addPropertyDomain(propertyId, randomDomain);
                addPropertyRange(propertyId, randomRange);
            }
        }
    }

    public OntologyGenerator(int numberOfClasses, int numberOfProperties, int numberOfInstances, String tcId,
                             ITreeGenerator treeGenerator) {
        this(numberOfClasses, numberOfProperties, numberOfInstances, tcId, treeGenerator, DEFAULT_DOMAIN_RANGE_P, DEFAULT_IS_WAKLY_TYPED);
    }

    public final static double DEFAULT_DOMAIN_RANGE_P = 0.25;

    /**
     * The probability that the domain/range is the root node.
     */
    private final double domainRangeP;

    /**
     * Also, in DBpedia ist z.B. der speziellste Typ von Arnold_Schwarzenegger dbo:Person. Er hat aber
     * eingehende Kanten von dbo:starring, was wiederum rdfs:range dbo:Actor hat, also eine speziellere Klasse.
     * Heißt also, wenn ich eine Kante vom Typ p einfüge, sollte ich als mögliche Subjekte Instanzen von sowohl
     * Super- als auch Subklassen von domain(p) einbeziehen, als Objekte sowohl Super- als auch Subklassen von range(p).
     * Typisch für DBpedia ist z.B. auch, dass Objekte gar keinen Typ haben (bzw. nur owl:Thing), man die Kanteninfo
     * dann aber für Typinferenz ausnutzen kann. Das haben wir z.B. damals für SDType gemacht.
     */
    private final boolean isWeaklyTyped;

    private static final boolean DEFAULT_IS_WAKLY_TYPED = true;


    private void addInstance(String instance, String classId) {
        instanceIds.add(instance);
        instanceTypes.put(instance, classId);

        Set<String> types = classTree.getAllChildrenOfNode(classId);
        types.add(classId);

        if (classInstancesNonTransitive.containsKey(classId)) {
            classInstancesNonTransitive.get(classId).add(instance);
        } else {
            Set<String> instanceSet = new HashSet<>();
            instanceSet.add(instance);
            classInstancesNonTransitive.put(classId, instanceSet);
        }

        if (propertyDomainInstances != null) {
            // we need to update:
            // - propertyDomainInstances
            // - instanceSubjectProperties
            for (Map.Entry<String, String> entry : propertyDomains.entrySet()) {
                if (types.contains(entry.getValue())) {
                    updatePropertyDomain(entry.getKey());
                }
            }
        }

        if (propertyRangeInstances != null) {
            for (Map.Entry<String, String> entry : propertyRanges.entrySet()) {
                if (types.contains(entry.getValue())) {
                    updatePropertyRange(entry.getKey());
                }
            }
        }
    }

    public final Random random = new Random(42);

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyGenerator.class);

    /**
     * For domain and range, abstract types are more common than very specific types.
     * Therefore, we can skew the draw so that abstract types are more likely than concrete types.
     *
     * @param p The probability that we go one inheritance level deeper. P must be between 0.0 (inclusive) and
     *          1.0 (exclusive).
     * @return A random class.
     */
    private String pickDomainRangeSkewed(double p) {
        String result = this.classTree.getRoot();
        while (this.classTree.getChildrenOfNode(result).size() > 0 && random.nextDouble() > p) {
            result = Util.randomDrawFromSet(classTree.getChildrenOfNode(result));
        }
        return result;
    }

    private Set<String> generateIds(int numberOfIds, String idComponent) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < numberOfIds; i++) {
            result.add("<" + idComponent + "_" + i + ">");
        }
        return result;
    }

    private void addPropertyDomain(String property, String domain) {
        propertyDomains.put(property, domain);
        updatePropertyDomain(property);
    }

    /**
     * Update indices for the provided property.
     *
     * @param property The property that is to be updated.
     */
    private void updatePropertyDomain(String property) {
        final String domain = getDomain(property);
        Set<String> instances = getClassInstancesTransitive(domain);
        propertyDomainInstances.put(property, instances);
        for (String instance : instances) {
            if (instanceSubjectProperties.containsKey(instance)) {
                instanceSubjectProperties.get(instance).add(property);
            } else {
                Set<String> properties = new HashSet<>();
                properties.add(property);
                instanceSubjectProperties.put(instance, properties);
            }
        }
    }

    /**
     * Update the property indices for domain and range.
     *
     * @param propertyId Identifier of the property.
     */
    private void updateProperty(String propertyId) {
        updatePropertyRange(propertyId);
        updatePropertyDomain(propertyId);
    }

    private void addPropertyRange(String property, String range) {
        propertyRanges.put(property, range);
        updatePropertyRange(property);
    }

    private void updatePropertyRange(String property) {
        final String range = getRange(property);
        Set<String> instances = getClassInstancesTransitive(range);
        propertyRangeInstances.put(property, instances);
    }

    public Set<String> getClassInstancesTransitive(String classId) {
        Set<String> allClasses = new HashSet<>();
        allClasses.add(classId);
        allClasses.addAll(classTree.getAllChildrenOfNode(classId));
        Set<String> result = new HashSet<>();
        for (String classIdLocal : allClasses) {
            Set<String> instances = classInstancesNonTransitive.get(classIdLocal);
            if (instances == null) {
                LOGGER.warn("No class instances found for class '" + classIdLocal + "'. " +
                        "This may happen in situations where there are many classes and few instances.");
            }
            result.addAll(instances);
        }
        return result;
    }

    private final String tcId;
    private final ITreeGenerator treeGenerator;
    Set<String> classIds;
    Set<String> propertyIds;
    Set<String> instanceIds;
    Tree classTree;

    Map<String, String> propertyRanges;
    Map<String, String> propertyDomains;

    /**
     * {@code instance -> <all possible property ids where domain(property)=instance >}
     */
    Map<String, Set<String>> instanceSubjectProperties;

    /**
     * {@code property -> <all possible range INSTANCE ids>}
     */
    Map<String, Set<String>> propertyRangeInstances;

    /**
     * {@code property -> <all possible domain INSTANCE ids>}
     */
    Map<String, Set<String>> propertyDomainInstances;

    /**
     * {@code class -> all instances (non transitive)}
     */
    Map<String, Set<String>> classInstancesNonTransitive;

    /**
     * {@code instance -> class id}
     */
    Map<String, String> instanceTypes;

    @Override
    public String getRandomInstanceId() {
        return Util.randomDrawFromSet(instanceIds);
    }

    @Override
    public String getRandomPredicateId() {
        return Util.randomDrawFromSet(propertyIds);
    }

    @Override
    public String getRandomClassId() {
        return Util.randomDrawFromSet(classIds);
    }

    @Override
    public String getRandomPredicateForInstance(String instanceId) {
        if (instanceSubjectProperties.get(instanceId) == null) {
            LOGGER.error("Did not find instance " + instanceId + " in instanceSubjectProperties.\n" +
                    "Program will fail.");
        }
        return Util.randomDrawFromSet(instanceSubjectProperties.get(instanceId));
    }

    @Override
    public Triple getRandomPredicateObjectForInstance(String nodeId) {
        String predicate = getRandomPredicateForInstance(nodeId);
        Set<String> instances = propertyRangeInstances.get(predicate);
        if (instances == null) {
            LOGGER.error("No property for instance id: " + nodeId);
            return null;
        }
        String object = Util.randomDrawFromSet(instances);
        return new Triple(nodeId, predicate, object);
    }

    /**
     * Obtain a random range instance of the specified property.
     *
     * @param edgeId Property id.
     * @return Random instance that fulfills range criterion.
     */
    @Override
    public String getRandomObjectNodeForInstance(String edgeId) {
        return Util.randomDrawFromSet(propertyRangeInstances.get(edgeId));
    }

    @Override
    public String getRandomSubjectNodeForPredicate(String edgeId) {
        return Util.randomDrawFromSet(propertyDomainInstances.get(edgeId));
    }

    @Override
    public Set<String> getClasses() {
        return this.classIds;
    }

    @Override
    public Set<String> getProperties() {
        return this.propertyIds;
    }

    @Override
    public Set<String> getInstances() {
        return this.instanceIds;
    }

    @Override
    public String getRange(String propertyId) {
        return propertyRanges.get(propertyId);
    }

    @Override
    public String getDomain(String propertyId) {
        return propertyDomains.get(propertyId);
    }

    @Override
    public Tree getClassTree() {
        return this.classTree;
    }

    @Override
    public String getInstanceType(String instance) {
        return instanceTypes.get(instance);
    }

    @Override
    public void ensureObjectNumberForPredicate(String predicateId, int objectNumber){
        int target = objectNumber - propertyRangeInstances.get(predicateId).size();
        if(target <= 0){
            LOGGER.info("Enough objects for " + predicateId + ". Nothing to generate.");
        } else {
            LOGGER.warn("Not enough objects for " + predicateId + ". Generating " + target + " additional nodes.");
            String targetClass = propertyRanges.get(predicateId);
            Set<String> desiredClasses = new HashSet<>(classTree.getAllChildrenOfNode(targetClass));
            desiredClasses.add(targetClass);
            for (int i = 0; i < target; i++) {
                addInstance("<EXTRA_RANGE_I_FOR_" + Util.removeTags(predicateId) + "_" + i + ">",
                        Util.randomDrawFromSet(desiredClasses));
            }
        }
    }

    @Override
    public void ensureSubjectNumberForPredicate(String predicateId, int subjectNumber) {
        int target = subjectNumber - propertyDomainInstances.get(predicateId).size();
        if (target <= 0) {
            LOGGER.info("Enough subjects for " + predicateId + ". Nothing to generate.");
        } else {
            LOGGER.warn("Not enough subjects for " + predicateId + ". Generating " + target + " additional nodes.");
            String targetClass = propertyDomains.get(predicateId);
            Set<String> desiredClasses = new HashSet<>(classTree.getAllChildrenOfNode(targetClass));
            desiredClasses.add(targetClass);
            for (int i = 0; i < target; i++) {
                addInstance("<EXTRA_DOMAIN_I_FOR_" + Util.removeTags(predicateId) + "_" + i + ">",
                        Util.randomDrawFromSet(desiredClasses));
            }
        }
    }

    @Override
    public void ensureEnoughInstancesOfType(String classId, int desiredNumber) {
        int target = desiredNumber - getInstancesOfTypeTransitive(classId).size();
        if (target <= 0) {
            LOGGER.info("Enough instances for class " + classId + ". Nothing to generate.");
        } else {
            for (int i = 0; i < target; i++) {
                addInstance("<EXTRA_I_FOR_CLASS_" + Util.removeTags(classId) + "_" + i + ">",
                        classId);
            }
        }
    }

    @Override
    public Set<String> getInstancesOfTypeTransitive(String classId) {
        Set<String> targetClasses = new HashSet<>(classTree.getAllChildrenOfNode(classId));
        targetClasses.add(classId);
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : classInstancesNonTransitive.entrySet()) {
            if (targetClasses.contains(entry.getKey())) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    public String getTcId() {
        return tcId;
    }

    public ITreeGenerator getTreeGenerator() {
        return treeGenerator;
    }

    public Set<String> getClassInstancesNonTransitive(String classId) {
        return classInstancesNonTransitive.get(classId);
    }

    public Set<String> getPropertyRangeInstances(String property) {
        return propertyRangeInstances.get(property);
    }

    public double getDomainRangeP() {
        return domainRangeP;
    }
}
