package d2t.terra.abubaria.io.graphics.window

enum class FpsLimit(val value: Int, val displayName: String) {
    VSYNC(360, "VSYNC"),
    FPS_30(30, "30 FPS"),
    FPS_60(60, "60 FPS"),
    FPS_120(120, "120 FPS"),
    FPS_144(144, "144 FPS"),
    FPS_240(240, "240 FPS"),
    FPS_360(360, "360 FPS"),
    CUSTOM(0, "Пользовательский") // Для произвольного значения
}