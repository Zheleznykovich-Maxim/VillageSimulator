package org.example.javafxui.genealogy;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreeRenderer {
    private double offsetX = 0;
    public void render(Pane pane, TreeNode n) {
        Group g = new Group();
        renderNode(g, n);
        pane.getChildren().add(g);
    }

    private void renderNode(Group g, TreeNode n) {
        Label label = new Label();
        label.setFont(Font.font("System", 12));

        if (n.nodeW < 60) {
            label.setText("Ж" + n.data.getId());
            label.setAlignment(Pos.BASELINE_CENTER);
        } else {
            label.setText("Житель " + n.data.getId());
        }

        label.setStyle(n.data.getStatus().getValue().equals("Alive")
                ? "-fx-background-color: lightgreen; -fx-border-color: black; -fx-padding: 5;"
                : "-fx-background-color: lightgray; -fx-border-color: black; -fx-padding: 5;");

        label.setLayoutX(n.x + offsetX);
        label.setLayoutY(n.y);
        label.setPrefSize(n.nodeW, n.nodeH);
        g.getChildren().add(label);

        for (TreeNode c : n.children) {
            renderNode(g, c);
            Line line = new Line(
                    n.x + n.nodeW / 2 + offsetX, n.y + n.nodeH + 5,
                    c.x + c.nodeW / 2 + offsetX, c.y
            );
            line.setStroke(Color.BLACK);
            g.getChildren().add(line);
        }
    }
}
