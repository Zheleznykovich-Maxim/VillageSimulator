package org.example.javafxui;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

import static org.example.javafxui.DrawTreeMarkup.*;

public class TreeNode {
    VillagerData data;
    List<TreeNode> children;

    // габариты одного узла (с padding)
    double nodeW, nodeH;

    // суммарная ширина поддерева
    double subtreeW;

    // позиция левый-вверх узла
    double x, y;

    Label label;

    public TreeNode(VillagerData data, List<TreeNode> children) {
        this.data = data;
        this.children = children;
    }

    public TreeNode(VillagerData data) {
        this.data = data;
    }

    public void calculateTree() {

        // 1) построим расчётное дерево
        buildTree(data);

        // 2) проставим размеры узлов (без добавления в сцену)
        measureNodes(this);

        // 3) посчитаем ширину поддеревьев (снизу вверх)
        computeSubtreeWidths(this);

        // 4) разложим координаты (сверху вниз), корень по центру
        double contentW = Math.max(this.subtreeW, MIN_NODE_WIDTH) + 2 * MARGIN;
        double contentH = computeTreeHeight(this) + 2 * MARGIN;
        layoutTree(this, (contentW - this.subtreeW) / 2.0 + MARGIN, MARGIN);
    }

    // построение дерева из VillagerData
    private TreeNode buildTree(VillagerData d) {
        List<TreeNode> kids = d.getChildren().stream().map(this::buildTree).toList();
        return new TreeNode(d, kids);
    }

    // измеряем текст (без добавления в сцену)
    private double measureTextWidth(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("System", FontWeight.NORMAL, 10));
        return t.getLayoutBounds().getWidth();
    }

    private double measureTextHeight() {
        // примерная высота строки текста + padding
        Text t = new Text("Ag");
        t.setFont(Font.font("System", FontWeight.NORMAL, 10));
        return t.getLayoutBounds().getHeight();
    }

    // задаём размеры каждого узла (nodeW/nodeH)
    private void measureNodes(TreeNode n) {
        // компактная подпись, если дерево широкое — можно настраивать по своему критерию
        String text = shouldUseCompactLabels(n) ? String.valueOf(n.data.getId()) : ("Житель " + n.data.getId());
        double w = Math.max(MIN_NODE_WIDTH, measureTextWidth(text) + NODE_HPADDING * 2);
        double h = measureTextHeight() + NODE_VPADDING * 2;

        n.nodeW = w;
        n.nodeH = h;

        for (TreeNode c : n.children) measureNodes(c);
    }

    // простая эвристика: если у узла много потомков в глубину/ширину — переходим на короткую подпись
    boolean shouldUseCompactLabels(TreeNode n) {
        // Например: если у узла >= 3 детей или глубина поддерева > 3
        return n.children.size() >= 3 || depth(n) > 3;
    }

    private int depth(TreeNode n) {
        if (n.children.isEmpty()) return 1;
        int m = 0;
        for (TreeNode c : n.children) m = Math.max(m, depth(c));
        return m + 1;
    }

    // считаем ширину поддерева: либо ширина узла, либо сумма ширин детей + зазоры
    private void computeSubtreeWidths(TreeNode n) {
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

    // расставляем координаты: родитель по центру над объединённой шириной детей
    private void layoutTree(TreeNode n, double leftX, double topY) {
        n.y = topY;
        if (n.children.isEmpty()) {
            // лист: ставим «по центру выделенной полосы»
            n.x = leftX + (n.subtreeW - n.nodeW) / 2.0;
            return;
        }
        // сначала раскладываем детей слева-направо
        double curX = leftX;
        for (TreeNode c : n.children) {
            layoutTree(c, curX, topY + n.nodeH + LEVEL_VGAP);
            curX += c.subtreeW + SUBTREE_HGAP;
        }
        // родителя ставим по центру объединенной полосы детей (или по центру собственной ширины, если он шире)
        double centerSpanLeft = leftX;
        double centerSpanRight = curX - SUBTREE_HGAP; // последний инкремент убираем
        double spanW = centerSpanRight - centerSpanLeft;
        double parentX = leftX + (Math.max(spanW, n.nodeW) - n.nodeW) / 2.0;
        n.x = parentX;
    }

    // вычисляем полную высоту дерева (для преф. размера панельки)
    private double computeTreeHeight(TreeNode n) {
        if (n.children.isEmpty()) return n.nodeH;
        double max = 0;
        for (TreeNode c : n.children) max = Math.max(max, computeTreeHeight(c));
        return n.nodeH + LEVEL_VGAP + max;
    }
}
