package org.example.javafxui;

import lombok.Getter;

@Getter
public class VillagerData {
    private final int id;
    private final int age;
    private final String status;

    public VillagerData(int id, int age, String status) {
        this.id = id;
        this.age = age;
        this.status = status;
    }
}
