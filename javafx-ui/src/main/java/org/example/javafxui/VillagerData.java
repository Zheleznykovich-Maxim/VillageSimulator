package org.example.javafxui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VillagerData {
    private int id;
    private IntegerProperty age = new SimpleIntegerProperty();
    private StringProperty status = new SimpleStringProperty();
    private final List<VillagerData> children = new ArrayList<>();

    public VillagerData(int id, int age, String status) {
        this.id = id;
        this.age.set(age);
        this.status.set(status);
    }

    public void addChild(VillagerData child) {
        children.add(child);
    }
    public void setAge(int value) { age.set(value); }
    public void setStatus(String value) { status.set(value); }
}
