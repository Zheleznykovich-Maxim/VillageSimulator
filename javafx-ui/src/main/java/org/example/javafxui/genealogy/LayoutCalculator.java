package org.example.javafxui.genealogy;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LayoutCalculator {

    private static final double NODE_HPADDING = 10;
    private static final double NODE_VPADDING = 5;
    private static final double MIN_NODE_WIDTH = 50;
    private static final double LEVEL_VGAP = 80;
    private static final double SUBTREE_HGAP = 30;

    public void measure(TreeNode n) {
        String text = getLabel(n);
        double w = Math.max(MIN_NODE_WIDTH, measureTextWidth(text) + NODE_HPADDING * 2);
        double h = measureTextHeight() + NODE_VPADDING * 2;
        n.nodeW = w;
        n.nodeH = h;
        n.children.forEach(this::measure);
    }

    public void computeSubtreeWidths(TreeNode n) {
        if (n.children.isEmpty()) {
            n.subtreeW = n.nodeW;
            return;
        }
        double sum = 0;
        for (TreeNode c : n.children) {
            computeSubtreeWidths(c);
            if (sum > 0) sum += SUBTREE_HGAP;
            sum += c.subtreeW;
        }
        n.subtreeW = Math.max(n.nodeW, sum);
    }

    public double layout(TreeNode root) {
        double totalWidth = root.subtreeW;
        layoutTree(root, (totalWidth - root.subtreeW) / 2, 20);
        return totalWidth;
    }

    private void layoutTree(TreeNode n, double leftX, double topY) {
        n.y = topY;
        if (n.children.isEmpty()) {
            n.x = leftX + (n.subtreeW - n.nodeW) / 2;
            return;
        }
        double curX = leftX;
        for (TreeNode c : n.children) {
            layoutTree(c, curX, topY + n.nodeH + LEVEL_VGAP);
            curX += c.subtreeW + SUBTREE_HGAP;
        }
        double spanW = curX - SUBTREE_HGAP - leftX;
        n.x = leftX + (Math.max(spanW, n.nodeW) - n.nodeW) / 2.0;
    }

    public double computeHeight(TreeNode root) {
        if (root.children.isEmpty()) return root.nodeH;
        double max = 0;
        for (TreeNode c : root.children) max = Math.max(max, computeHeight(c));
        return root.nodeH + LEVEL_VGAP + max;
    }

    private String getLabel(TreeNode n) {
        return (n.children.size() >= 3 ? String.valueOf(n.data.getId()) : "Житель " + n.data.getId());
    }

    private double measureTextWidth(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("System", 12));
        return t.getLayoutBounds().getWidth();
    }

    private double measureTextHeight() {
        Text t = new Text("Ag");
        t.setFont(Font.font("System", 12));
        return t.getLayoutBounds().getHeight();
    }
}
