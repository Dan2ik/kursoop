package org.example.kurs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Clock {
    private LocalTime currentTime;
    private DayOfWeek currentDay;
    private final DateTimeFormatter formatter; // Формат времени
    private final Timeline timeline;           // Анимация для обновления времени
    public static double speed = 1.0;          // Скорость (1x по умолчанию)
    private Runnable onNewDayCallback;

    public Clock(LocalTime startTime, DayOfWeek startDay) {
        this.currentTime = startTime;
        this.currentDay = startDay; // Устанавливаем начальный день недели
        this.formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    // Метод запуска часов
    public void start() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1 / speed), e -> {
            currentTime = currentTime.plusMinutes(1);
            if (currentTime.equals(LocalTime.MIDNIGHT)) {
                currentDay = currentDay.plus(1);
                if (currentDay.getValue() > 7) {
                    currentDay = DayOfWeek.MONDAY;
                }
                if (onNewDayCallback != null) {
                    onNewDayCallback.run();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Метод остановки часов
    public void stop() {
        timeline.stop();
    }

    // Метод для изменения скорости времени
    public void setSpeed(double speed) {
        Clock.speed = speed;
        timeline.setRate(speed);  // Устанавливаем скорость для таймера
    }

    public static double getSpeed() {
        return speed;
    }

    // Метод обновления времени
    private void updateClock() {
        currentTime = currentTime.plusSeconds((long) speed); // Увеличиваем время с учетом скорости

        // Если прошли за полночь, переключаем на следующий день
        if (currentTime.isAfter(LocalTime.MAX)) {
            currentTime = LocalTime.MIN; // Сброс времени на 00:00
            currentDay = currentDay.plus(1); // Переход к следующему дню
            if (currentDay == DayOfWeek.MONDAY.plus(7)) { // Зацикливание недели
                currentDay = DayOfWeek.MONDAY;
            }
        }
    }

    // Получить текущее время в виде строки
    public String getCurrentTime() {
        return currentTime.format(formatter);
    }

    // Получить текущий день недели
    public DayOfWeek getCurrentDay() {
        return currentDay;
    }
    public void setOnNewDay(Runnable callback) {
        this.onNewDayCallback = callback;
    }

    public LocalTime getHour() {
        return currentTime;
    }
}
