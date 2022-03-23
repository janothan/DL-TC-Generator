package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import java.util.*;

public class ConstantSplitTreeGenerator implements ITreeGenerator {


    final int avgBranchingFactor;

    /**
     * Constructor
     *
     * @param avgBranchingFactor The number of leaves per node. If no hierarchy is desired, set the {@code splitNumber}
     *                    to a very high number.
     */
    public ConstantSplitTreeGenerator(int avgBranchingFactor) {
        this.avgBranchingFactor = avgBranchingFactor;
    }

    @Override
    public Tree generateTree(Set<String> nodes, String rootNode) {
        Tree result = new Tree(rootNode);

        int currentCounter = 0;
        String currentWorkNode = rootNode;
        LinkedList<String> workList = new LinkedList<>();

        for (String node : nodes){
            if(node.equals(rootNode)){
                continue;
            }
            if(currentCounter == avgBranchingFactor){
                currentWorkNode = workList.removeFirst();
                result.addLeaf(currentWorkNode, node);
                currentCounter = 0;
            }
            result.addLeaf(currentWorkNode, node);
            workList.add(node);
            currentCounter++;
        }

        return result;
    }

    public int getAvgBranchingFactor() {
        return avgBranchingFactor;
    }
}
