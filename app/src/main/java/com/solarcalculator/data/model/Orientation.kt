package com.solarcalculator.data.model

enum class Orientation(val displayName: String, val azimuthAngle: Double, val efficiencyFactor: Double) {
    SOUTH("正南", 180.0, 1.0),
    SOUTHEAST("东南", 135.0, 0.95),
    SOUTHWEST("西南", 225.0, 0.95),
    EAST("正东", 90.0, 0.85),
    WEST("正西", 270.0, 0.85),
    NORTH("正北", 0.0, 0.70);

    companion object {
        fun fromDisplayName(name: String): Orientation {
            return values().find { it.displayName == name } ?: SOUTH
        }
    }
}
