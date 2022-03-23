package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OntologyGenerator {


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
        propertyRangeMap = new HashMap<>();
        propertyRangeInstances = new HashMap<>();
        propertyDomainMap = new HashMap<>();
        propertyDomainInstances = new HashMap<>();
        instanceSubjectProperties = new HashMap<>();

        int firstProperties = 0;
        for (String propertyId : propertyIds) {
            if (firstProperties < 3) {
                // In cases where there are very few properties, having just one domain=root and range=root property
                // is insufficient to avoid infinity loops.
                addPropertyDomain(propertyId, classTree.getRoot());
                addPropertyRange(propertyId, classTree.getRoot());
                firstProperties++;
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
        this(numberOfClasses, numberOfProperties, numberOfInstances, tcId, treeGenerator, DEFAULT_DOMAIN_RANGE_P, DEFAULT_IS_WEAKLY_TYPED);
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

    private static final boolean DEFAULT_IS_WEAKLY_TYPED = true;


    private void addInstance(String instance, String classId) {
        instanceIds.add(instance);
        instanceTypes.put(instance, classId);

        Set<String> types = classTree.getAllParentsOfNode(classId);
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
            for (Map.Entry<String, String> entry : propertyDomainMap.entrySet()) {
                if (types.contains(entry.getValue())) {
                    updatePropertyDomain(entry.getKey());
                }
            }
        }

        if (propertyRangeInstances != null) {
            for (Map.Entry<String, String> entry : propertyRangeMap.entrySet()) {
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
        propertyDomainMap.put(property, domain);
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
        propertyRangeMap.put(property, range);
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

    /**
     * Map from the property to its range.
     */
    Map<String, String> propertyRangeMap;

    /**
     * Map from the property to its domain.
     */
    Map<String, String> propertyDomainMap;

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

    public String getRandomInstanceId() {
        return Util.randomDrawFromSet(instanceIds);
    }

    public String getRandomPropertyId() {
        return Util.randomDrawFromSet(propertyIds);
    }

    /**
     * {@code predicate.domain = predicate.range}
     * @return Some randomly drawn predicate ID where the domain is equal to the range.
     */
    public String getRandomPropertyIdWhereDomainIsRange() {
        Set<String> resultSet = new HashSet<>();
        for(String property : propertyIds){
            if(getDomain(property).equals(getRange(property))){
                resultSet.add(property);
            }
        }
        if(resultSet.size() == 0){
            LOGGER.error("There is no property where property.range = property.domain!");
        }
        return Util.randomDrawFromSet(resultSet);
    }

    public String getRandomClassId() {
        return Util.randomDrawFromSet(classIds);
    }

    /**
     * Generate a triple where {@code nodeId} is in subject position.
     * @param nodeId Subject node ID.
     * @return Triple.
     */
    public Triple getRandomTripleWithSubject(String nodeId) {
        String predicate = getRandomPropertyWhereInstanceIsDomain(nodeId);
        Set<String> instances = propertyRangeInstances.get(predicate);
        if (instances == null) {
            LOGGER.error("No property for instance id: " + nodeId);
            return null;
        }
        String object = Util.randomDrawFromSet(instances);
        return new Triple(nodeId, predicate, object);
    }

    /**
     * Returns an Instance ID where the instance is of type classId or child of classId -- i.e., types are
     * transitively resolved.
     * @param classId The type.
     * @return InstanceID.
     */
    public String getRandomInstanceOfTypeTransitive(String classId){
        return Util.randomDrawFromSet(getInstancesOfTypeTransitive(classId));
    }

    /**
     * Generate a triple where {@code instanceId} is in subject position.
     * @param instanceId Subject node ID.
     * @return Triple.
     */
    public Triple getRandomTripleWithObject(String instanceId){
        String property = getRandomPropertyWhereInstanceIsRange(instanceId);
        String subject = getRandomInstanceOfTypeTransitive(getDomain(property));
        return new Triple(subject, property,instanceId);
    }

    /**
     * Generates triple s,p,o where o=objectInstanceId
     * @param objectInstanceId Object instance id.
     * @param subjectTypeId Subject type id.
     * @return The triple.
     */
    public Triple getRandomTripleWithObjectWhereSubjectOfType(String objectInstanceId, String subjectTypeId){
        Set<String> properties = getPropertiesWithDomainRange(
                subjectTypeId, getInstanceType(objectInstanceId
        ));
        String property = Util.randomDrawFromSet(properties);
        String subject = getRandomSubjectForProperty(property);
        return new Triple(subject, property, objectInstanceId);
    }

    public Triple getRandomTripleWithSubjectWhereObjectOfType(String subjectInstanceId, String objectTypeId){
        Set<String> properties = getPropertiesWithDomainRange(
                getInstanceType(subjectInstanceId), objectTypeId);
        String property = Util.randomDrawFromSet(properties);
        String object = getRandomObjectForProperty(property);
        return new Triple(subjectInstanceId, property, object);
    }

    /**
     * Domain and range are interpreted here as lower bounds!
     * @param domain Lower domain bound.
     * @param range Lower range bound.
     * @return Set of properties.
     */
    public Set<String> getPropertiesWithDomainRange(String domain, String range){
        Set<String> rangeClasses = new HashSet<>(classTree.getAllParentsOfNode(range));
        rangeClasses.add(range);

        Set<String> domainClasses = new HashSet<>(classTree.getAllParentsOfNode(domain));
        domainClasses.add(domain);

        Set<String> resultIds = new HashSet<>();
        for(Map.Entry<String, String> entry : propertyRangeMap.entrySet()){
            if(rangeClasses.contains(entry.getValue()) &&
                domainClasses.contains(propertyDomainMap.get(entry.getKey()))
            ){
                resultIds.add(entry.getKey());
            }
        }
        return resultIds;
    }

    /**
     * Obtain a random range instance of the specified property.
     *
     * @param edgeId Property id.
     * @return Random instance that fulfills range criterion.
     */
    public String getRandomObjectForProperty(String edgeId) {
        return Util.randomDrawFromSet(propertyRangeInstances.get(edgeId));
    }

    public String getRandomSubjectForProperty(String edgeId) {
        return Util.randomDrawFromSet(propertyDomainInstances.get(edgeId));
    }

    public Set<String> getClasses() {
        return this.classIds;
    }

    public Set<String> getProperties() {
        return this.propertyIds;
    }

    public Set<String> getInstances() {
        return this.instanceIds;
    }

    public String getRange(String propertyId) {
        return propertyRangeMap.get(propertyId);
    }

    public String getDomain(String propertyId) {
        return propertyDomainMap.get(propertyId);
    }

    public Tree getClassTree() {
        return this.classTree;
    }

    public String getInstanceType(String instance) {
        return instanceTypes.get(instance);
    }

    /**
     * Property where getChildrenOfNode(property.domain) > 2.
     * Note that we require really two direct children (not 1 child which has again a child).
     * @return Property ID
     */
    public String getRandomPropertyWhereDomainHasAtLeastTwoSubtypes(){
        return Util.randomDrawFromSet(getPropertiesWhereDomainHasAtLeastTwoSubtypes());
    }

    public Set<String> getPropertiesWhereDomainHasAtLeastTwoSubtypes(){
        Set<String> propertyCandidates = new HashSet<>();
        for ( Map.Entry<String, String> entry : propertyDomainMap.entrySet() ) {
            if(classTree.getChildrenOfNode(entry.getValue()).size() >= 2){
                propertyCandidates.add(entry.getKey());
            }
        }
        return propertyCandidates;
    }

    /**
     * Property where getChildrenOfNode(property.range) > 2.
     * Note that we require really two direct children (not 1 child which has again a child).
     * @return Property ID
     */
    public String getRandomPropertyWhereRangeHasAtLeastTwoSubtypes(){
        return Util.randomDrawFromSet(getPropertiesWhereRangeHasAtLeastTwoSubtypes());
    }

    /**
     *
     * @return Properties where getSubtypes(property.range) >= 2
     */
    public Set<String> getPropertiesWhereRangeHasAtLeastTwoSubtypes(){
        Set<String> propertyCandidates = new HashSet<>();
        for ( Map.Entry<String, String> entry : propertyRangeMap.entrySet() ) {
            if(classTree.getChildrenOfNode(entry.getValue()).size() >= 2){
                propertyCandidates.add(entry.getKey());
            }
        }
        return propertyCandidates;
    }

    /**
     * Ensure that there are at least {@code objectNumber} different objects for the provided {@code predicateId}.
     * @param predicateId Predicate ID.
     * @param objectNumber Number of desired unique objects.
     */
    public void ensureObjectNumberForProperty(String predicateId, int objectNumber){
        int target = objectNumber - propertyRangeInstances.get(predicateId).size();
        if(target <= 0){
            LOGGER.info("Enough objects for " + predicateId + ". Nothing to generate.");
        } else {
            LOGGER.warn("Not enough objects for " + predicateId + ". Generating " + target + " additional nodes.");
            String targetClass = propertyRangeMap.get(predicateId);
            Set<String> desiredClasses = new HashSet<>(classTree.getAllChildrenOfNode(targetClass));
            desiredClasses.add(targetClass);
            for (int i = 0; i < target; i++) {
                addInstance("<EXTRA_RANGE_I_FOR_" + Util.removeTags(predicateId) + "_" + i + ">",
                        Util.randomDrawFromSet(desiredClasses));
            }
        }
    }

    /**
     * Ensure that there are at least {@code subjectNumber} different subjects for the provided {@code predicateId}.
     * @param predicateId Predicate ID.
     * @param subjectNumber Number of desired unique subjects.
     */
    public void ensureSubjectNumberForProperty(String predicateId, int subjectNumber) {
        int target = subjectNumber - propertyDomainInstances.get(predicateId).size();
        if (target <= 0) {
            LOGGER.info("Enough subjects for " + predicateId + ". Nothing to generate.");
        } else {
            LOGGER.warn("Not enough subjects for " + predicateId + ". Generating " + target + " additional nodes.");
            String targetClass = propertyDomainMap.get(predicateId);
            Set<String> desiredClasses = new HashSet<>(classTree.getAllChildrenOfNode(targetClass));
            desiredClasses.add(targetClass);
            for (int i = 0; i < target; i++) {
                addInstance("<EXTRA_DOMAIN_I_FOR_" + Util.removeTags(predicateId) + "_" + i + ">",
                        Util.randomDrawFromSet(desiredClasses));
            }
        }
    }

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

    public void ensurePropertyWithRangeInstance(String instanceId, int desiredNumber) {
        int actual = getPropertiesWhereInstanceIsRange(instanceId).size();
        int target = desiredNumber - actual;
        if(target <= 0) {
            LOGGER.error("Enough properties with range '" + instanceId + "' exist.");
        } else {
            LOGGER.error("Generating " + target + " properties with range '" + instanceId + "'.");
            for(int i = 0; i < target; i++){
                String propertyId = "<P_EXTRA_RANGE_" + i + "_" +  Util.removeTags(instanceId) +  ">";
                propertyIds.add(propertyId);
                addPropertyRange(propertyId, getInstanceType(instanceId));
                addPropertyDomain(propertyId, pickDomainRangeSkewed(domainRangeP));
            }
        }
    }

    public void ensurePropertyWithDomainInstance(String instanceId, int desiredNumber){
        int actual = getPropertiesWhereInstanceIsDomain(instanceId).size();
        int target = desiredNumber - actual;
        if(target <= 0) {
            LOGGER.error("Enough properties with domain '" + instanceId + "' exist.");
        } else {
            LOGGER.error("Generating " + target + " properties with range '" + instanceId + "'.");
            for(int i = 0; i < target; i++) {
                String propertyId = "<P_EXTRA_DOMAIN_" + i + "_" + Util.removeTags(instanceId) + ">";
                propertyIds.add(propertyId);
                addPropertyDomain(propertyId, getInstanceType(instanceId));
                addPropertyRange(propertyId, pickDomainRangeSkewed(domainRangeP));
            }
        }
    }

    /**
     * Transitively resolved!
     * @param instanceId Instance id.
     * @return Set of properties.
     */
    public Set<String> getPropertiesWhereInstanceIsDomain(String instanceId){
        Set<String> result = new HashSet<>();
        for(Map.Entry<String, Set<String>> entry : propertyDomainInstances.entrySet()){
            if (entry.getValue().contains(instanceId)){
                result.add(entry.getKey());
            }
        }
        if(result.size() == 0){
            LOGGER.warn("There are no properties with domain including '" + instanceId + "'");
        }
        return result;
    }

    public Set<String> getPropertiesWhereInstanceIsRange(String instanceId){
        Set<String> result = new HashSet<>();
        for(Map.Entry<String, Set<String>> entry : propertyRangeInstances.entrySet()){
            if (entry.getValue().contains(instanceId)){
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public String getRandomPropertyWhereInstanceIsDomain(String instanceId){
        return Util.randomDrawFromSet(getPropertiesWhereInstanceIsDomain(instanceId));
    }

    public String getRandomPropertyWhereInstanceIsRange(String instanceId){
        return Util.randomDrawFromSet(getPropertiesWhereInstanceIsRange(instanceId));
    }

}
