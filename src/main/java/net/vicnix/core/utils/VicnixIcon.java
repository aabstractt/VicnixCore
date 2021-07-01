package net.vicnix.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VicnixIcon {

    HEART(1, "Heart", "❤"),
    UWU(2, "UwU", "UwU");

    private final int id;
    private final String name;
    private final String format;

    public static VicnixIcon of(int id) {
        for (VicnixIcon icon : values()) {
            if (icon.getId() == id) {
                return icon;
            }
        }

        return null;
    }

    public static VicnixIcon of(String name) {
        for (VicnixIcon icon : values()) {
            if (icon.getName().equalsIgnoreCase(name)) {
                return icon;
            }
        }

        return null;
    }
}