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
    public int CountDays = 0;
    private LocalTime oldtime;

    public Clock(LocalTime startTime, DayOfWeek startDay) {
        this.currentTime = startTime;
        this.currentDay = startDay; // Устанавливаем начальный день недели
        this.formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.oldtime = startTime; // Инициализируем oldtime начальным временем

        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    // Метод запуска часов
    public void start() {
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
        if (oldtime.isAfter(currentTime)) {
            currentTime = LocalTime.MIN; // Сброс времени на 00:00
            CountDays += 1;
            currentDay = currentDay.plus(1); // Переход к следующему дню
            if (currentDay == DayOfWeek.SUNDAY.plus(1)) {
                currentDay = DayOfWeek.MONDAY; // Сброс на понедельник после воскресенья
            }
            if (onNewDayCallback != null) {
                onNewDayCallback.run();
            }
        }
        oldtime = currentTime;
    }

    public int getCountDays() {
        return CountDays;
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
        System.out.println("новый день");
        this.onNewDayCallback = callback;
    }

    public LocalTime getHour() {
        return currentTime;
    }
}
