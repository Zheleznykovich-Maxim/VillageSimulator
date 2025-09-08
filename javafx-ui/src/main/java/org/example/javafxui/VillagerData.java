package org.example.javafxui;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VillagerData {
    private int id;
    private int age;
    private String status;
    private final List<VillagerData> children = new ArrayList<>();

    public VillagerData(int id, int age, String status) {
        this.id = id;
        this.age = age;
        this.status = status;
    }

    public void addChild(VillagerData child) {
        children.add(child);
    }


}
