package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import java.util.*;

public class ConstantSplitTreeGenerator implements ITreeGenerator {


    final int splitNumber;
    final String rootNode;
    final Set<String> nodes;

    public ConstantSplitTreeGenerator(int splitNumber, String rootNode, Set<String> nodes) {
        this.splitNumber = splitNumber;
        this.nodes = nodes;
        this.rootNode = rootNode;
    }

    @Override
    public Tree generateTree() {
        Tree result = new Tree(this.rootNode);

        int currentCounter = 0;
        String currentWorkNode = this.rootNode;
        LinkedList<String> workList = new LinkedList<>();

        for (String node : this.nodes){
            if(node.equals(this.rootNode)){
                continue;
            }
            if(currentCounter == splitNumber){
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
}
