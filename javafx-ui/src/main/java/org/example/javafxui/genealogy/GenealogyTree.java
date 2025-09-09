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
    public double getContentHeight() {
        return root.getMaxY() + 40; // +40 запас (линии и отступ снизу)
    }
    public int getNodeCount() {
        return countNodes(root);
    }

    private int countNodes(TreeNode node) {
        int count = 1; // текущая нода
        for (TreeNode c : node.children) {
            count += countNodes(c);
        }
        return count;
    }

}
