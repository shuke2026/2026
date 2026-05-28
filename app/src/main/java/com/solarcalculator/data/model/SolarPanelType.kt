package com.solarcalculator.data.model

enum class SolarPanelType(val displayName: String, val efficiency: Double) {
    MONO_CRYSTALLINE("单晶硅", 0.20),
    POLY_CRYSTALLINE("多晶硅", 0.17),
    THIN_FILM("薄膜", 0.12),
    PERC("PERC", 0.22),
    HJT("HJT异质结", 0.24),
    TOPCON("TOPCon", 0.25);

    companion object {
        fun fromDisplayName(name: String): SolarPanelType {
            return values().find { it.displayName == name } ?: MONO_CRYSTALLINE
        }
    }
}
