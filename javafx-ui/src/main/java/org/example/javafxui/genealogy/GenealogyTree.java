package org.example.javafxui.genealogy;

import lombok.Getter;
import org.example.javafxui.VillagerData;

@Getter
public class GenealogyTree {

    private TreeNode root;
    private double contentWidth;
    private double contentHeight;

    public GenealogyTree(VillagerData rootData) {
        this.root = TreeNode.build(rootData);
    }

    public void layout() {
        LayoutCalculator calculator = new LayoutCalculator();
        calculator.measure(root);
        calculator.computeSubtreeWidths(root);

        contentWidth = calculator.layout(root);
        contentHeight = calculator.computeHeight(root);
    }

    public TreeNode getRootNode() {
        return root;
    }

}
