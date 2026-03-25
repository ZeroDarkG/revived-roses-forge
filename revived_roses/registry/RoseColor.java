package com.dalterdile.revived_roses.registry;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum RoseColor implements StringRepresentable {
    RED("red"),
    BLUE("blue");

    private final String name;

    RoseColor(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}