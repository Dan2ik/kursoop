package org.example.kurs;

import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Habitat {
    private final Pane pane; // Контейнер для добавления кругов
    private final Circle centre;
    private final Circle entry;
    private final Circle consultant;
    private final Circle cash;
    private final Circle exit;
    private final List<Circle> customers; // Список всех покупателей
    private final Random random = new Random();
    private boolean choice;
    private int cashCount = 1;          // Количество касс
    private int consultantCount = 1;   // Количество консультантов

    public Habitat(Pane pane, Circle centre, Circle entry, Circle consultant, Circle cash, Circle exit) {
        this.pane = pane;
        this.centre = centre;
        this.entry = entry;
        this.consultant = consultant;
        this.cash = cash;
        this.exit = exit;
        this.customers = new ArrayList<>();
    }

    // Метод для генерации нового покупателя
    public void generateCustomer() {
        Circle customer = new Circle(10, Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));

        pane.getChildren().add(customer); // Добавление на панель
        customers.add(customer); // Добавление в список

        // Случайный выбор маршрута
        choice = Math.random() >= 0.5;

        // Анимация покупателя
        animateCustomer(customer, choice);
    }

    // Метод для анимации движения покупателя
    private void animateCustomer(Circle customer, boolean choice) {
        // Устанавливаем стартовые координаты покупателя

        if (!pane.getChildren().contains(customer)) {
            pane.getChildren().add(customer);
        }

        // Очистка текущей анимации
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().clear();

        // Координаты ключевых точек
        List<Circle> waypoints = new ArrayList<>();
        waypoints.add(entry); // Центр
        waypoints.add(centre); // Центр
        if (choice) {
            waypoints.add(consultant); // Консультант
        } else {
            waypoints.add(cash); // Касса
        }
        waypoints.add(centre);
        waypoints.add(exit); // Выход

        // Создание анимации пути
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Circle currentPoint = waypoints.get(i);
            Circle nextPoint = waypoints.get(i + 1);

            Path path = new Path(
                    new MoveTo(currentPoint.getLayoutX(), currentPoint.getLayoutY()),
                    new LineTo(nextPoint.getLayoutX(), nextPoint.getLayoutY())
            );

            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(customer);
            pathTransition.setPath(path);
            pathTransition.setDuration(Duration.seconds(2)); // Продолжительность перехода

            sequentialTransition.getChildren().add(pathTransition);

            // Пауза на ключевых точках
            if (i == 0) {
                PauseTransition pause = new PauseTransition(Duration.seconds(1)); // Пауза в центре
                sequentialTransition.getChildren().add(pause);
            }
        }

        // Завершение анимации
        sequentialTransition.setOnFinished(e -> pane.getChildren().remove(customer));
        sequentialTransition.play();
    }


    private void animateCustomer1(Circle customer, boolean choice) {
        // Координаты центра
        double centrex = centre.getLayoutX();
        double centrey = centre.getLayoutY();

        // Координаты консультанта
        double consultantx = consultant.getLayoutX();
        double consultanty = consultant.getLayoutY();

        // Координаты кассы
        double cashx = cash.getLayoutX();
        double cashy = cash.getLayoutY();

        // Координаты выхода
        double exitx = exit.getLayoutX();
        double exity = exit.getLayoutY();

        // Движение к центру
        TranslateTransition toCenter = new TranslateTransition(Duration.seconds(2), customer);
        toCenter.setByX(centrex - customer.getLayoutX());
        toCenter.setByY(centrey - customer.getLayoutY());

        // Пауза в центре
        PauseTransition pauseAtCenter = new PauseTransition(Duration.seconds(1));

        // Движение от центра к консультанту
        TranslateTransition toConsultant = new TranslateTransition(Duration.seconds(2), customer);
        toConsultant.setByX(consultantx - centrex);
        toConsultant.setByY(consultanty - centrey);

        // Движение от центра к кассе
        TranslateTransition toCash = new TranslateTransition(Duration.seconds(2), customer);
        toCash.setByX(cashx - centrex);
        toCash.setByY(cashy - centrey);

        // Движение от центра к выходу
        TranslateTransition toExit = new TranslateTransition(Duration.seconds(2), customer);
        toExit.setByX(exitx - centrex);
        toExit.setByY(exity - centrey);

        // Выбор маршрута
        SequentialTransition sequentialTransition;
        if (choice) {
            sequentialTransition = new SequentialTransition(toCenter, pauseAtCenter, toConsultant, toCenter, toExit);
        } else {
            sequentialTransition = new SequentialTransition(toCenter, pauseAtCenter, toCash, toCenter,toExit);
        }
        // Запуск анимации
        sequentialTransition.setOnFinished(e -> pane.getChildren().remove(customer)); // Удаление покупателя после завершения анимации
        sequentialTransition.play();
    }

    // Метод для обновления параметров симуляции
    public void updateParameters(int cashCount, int consultantCount) {
        this.cashCount = cashCount;
        this.consultantCount = consultantCount;
        // Можно добавить логику перераспределения покупателей
    }

    // Метод для получения всех покупателей
    public List<Circle> getCustomers() {
        return customers;
    }
}
