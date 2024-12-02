package org.example.kurs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Clock {
    private LocalTime currentTime;
    private final DateTimeFormatter formatter;  // Формат времени
    private final Timeline timeline;            // Анимация для обновления времени
    public static double speed = 1.0;                 // Скорость (1x по умолчанию)

    public Clock(LocalTime startTime) {
        this.currentTime = startTime;
        this.formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    // Метод запуска часов
    public void start() {
        timeline.play();
    }
    public static double getSpeed() {
        return speed;
    }

    // Метод остановки часов
    public void stop() {
        timeline.stop();
    }

    // Метод для изменения скорости времени
    public void setSpeed(double speed) {
        this.speed = speed;
        timeline.setRate(speed);  // Устанавливаем скорость для таймера
    }

    // Метод обновления времени
    private void updateClock() {
        currentTime = currentTime.plusSeconds((long) speed);  // Увеличиваем время с учетом скорости
    }

    // Получить текущее время в виде строки
    public String getCurrentTime() {
        return currentTime.format(formatter);
    }
}
