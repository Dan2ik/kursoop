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
import java.util.concurrent.TimeUnit;

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

    private final List<CashRegister> cashRegisters;
    private final List<Consultant> consultants;

    public Habitat(Pane pane, Circle centre, Circle entry, Circle consultant, Circle cash, Circle exit, int cashCount, int consultantCount) {
        this.pane = pane;
        this.centre = centre;
        this.entry = entry;
        this.consultant = consultant;
        this.cash = cash;
        this.exit = exit;
        this.customers = new ArrayList<>();
        this.cashRegisters = new ArrayList<>();
        this.consultants = new ArrayList<>();

        // Создание касс и консультантов
        for (int i = 0; i < cashCount; i++) {
            this.cashRegisters.add(new CashRegister(i + 1, 5)); // Максимальная длина очереди = 5
        }

        for (int i = 0; i < consultantCount; i++) {
            this.consultants.add(new Consultant(i + 1, 5)); // Максимальная длина очереди = 5
        }
    }

    // Метод для генерации нового покупателя
    public void generateCustomer() throws InterruptedException {
        Circle customer = new Circle(10, Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));

        pane.getChildren().add(customer); // Добавление на панель
        customers.add(customer); // Добавление в список

        // Случайный выбор маршрута
        choice = Math.random() >= 0.5;

        // Анимация покупателя
        animateCustomer(customer, choice);
    }

    // Метод для анимации движения покупателя
    //waypoints.add(centre);
    // waypoints.add(exit); // Выход
    private void animateCustomertoexit(Circle customer, boolean choice) {
        // Устанавливаем стартовые координаты покупателя

        if (!pane.getChildren().contains(customer)) {
            pane.getChildren().add(customer);
        }

        // Очистка текущей анимации
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().clear();

        // Координаты ключевых точек
        List<Circle> waypoints = new ArrayList<>();

        waypoints.add(centre); // Центр
         waypoints.add(exit); // Выход
        if (choice) {
            waypoints.add(consultant); // Консультант
        } else {
            waypoints.add(cash); // Касса
        }
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

        // В момент нахождения клиента у консультанта или кассы
        sequentialTransition.setOnFinished(e -> {
            if (choice) {
                // Выбираем консультанта и добавляем в его очередь
                Consultant selectedConsultant = consultants.get(0); // Можно добавить логику для выбора консультанта
                boolean added = selectedConsultant.addCustomer(customer);
                selectedConsultant.assistCustomer();
                if (!added) {
                    System.out.println("Консультант " + selectedConsultant.getId() + " не смог принять клиента, очередь полна");
                }
            } else {
                // Выбираем кассу и добавляем в её очередь
                CashRegister selectedCashRegister = cashRegisters.get(0); // Можно добавить логику для выбора кассы
                boolean added = selectedCashRegister.addCustomer(customer);
                selectedCashRegister.serveCustomer();
                if (!added) {
                    System.out.println("Касса " + selectedCashRegister.getId() + " не смогла принять клиента, очередь полна");
                }
            }

            // Удаляем клиента из панели после завершения анимации
            pane.getChildren().remove(customer);
        });

        // Запускаем анимацию
        sequentialTransition.play();
    }

    private void animateCustomer(Circle customer, boolean choice) {
        if (!pane.getChildren().contains(customer)) {
            pane.getChildren().add(customer);
        }

        // Очистка текущей анимации
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().clear();

        // Координаты ключевых точек
        List<Circle> waypoints = new ArrayList<>();
        waypoints.add(entry);      // Вход
        waypoints.add(centre);     // Центр
        if (choice) {
            waypoints.add(consultant); // Консультант
        } else {
            waypoints.add(cash);       // Касса
        }
        waypoints.add(centre);
        waypoints.add(exit);       // Выход

        // Создание анимации пути
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Circle currentPoint = waypoints.get(i);
            Circle nextPoint = waypoints.get(i + 1);

            // Путь от текущей точки к следующей
            Path path = new Path(
                    new MoveTo(currentPoint.getLayoutX(), currentPoint.getLayoutY()),
                    new LineTo(nextPoint.getLayoutX(), nextPoint.getLayoutY())
            );

            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(customer);
            pathTransition.setPath(path);
            pathTransition.setDuration(Duration.seconds(2)); // Продолжительность перехода

            sequentialTransition.getChildren().add(pathTransition);
            if ((choice && nextPoint == consultant) || (!choice && nextPoint == cash)) {
                pathTransition.setOnFinished(e -> {
                    if (choice) {
                        // Работа с консультантом
                        Consultant selectedConsultant = consultants.get(0); // Логика выбора консультанта
                        boolean added = selectedConsultant.addCustomer(customer);
                        selectedConsultant.assistCustomer();
                        if (!added) {
                            System.out.println("Консультант " + selectedConsultant.getId() + " не смог принять клиента, очередь полна");
                        }
                    } else {
                        // Работа с кассой
                        CashRegister selectedCashRegister = cashRegisters.get(0); // Логика выбора кассы
                        boolean added = selectedCashRegister.addCustomer(customer);
                        selectedCashRegister.serveCustomer();
                        if (!added) {
                            System.out.println("Касса " + selectedCashRegister.getId() + " не смогла принять клиента, очередь полна");
                        }
                    }
                });
            }

            // Добавляем паузу на ключевых точках
            if (i == 1) { // Пауза в центре
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                sequentialTransition.getChildren().add(pause);
            }
        }

        // Действия при завершении пути
        sequentialTransition.setOnFinished(e -> {
            // Удаление клиента после выхода
            pane.getChildren().remove(customer);
        });

        // Запуск анимации
        sequentialTransition.play();
    }



    // Метод для обновления параметров симуляции
    public void updateParameters(int cashCount, int consultantCount) {
        this.cashRegisters.clear();
        this.consultants.clear();

        // Создание нового количества касс и консультантов
        for (int i = 0; i < cashCount; i++) {
            this.cashRegisters.add(new CashRegister(i + 1, 5)); // Максимальная длина очереди = 5
        }

        for (int i = 0; i < consultantCount; i++) {
            this.consultants.add(new Consultant(i + 1, 5)); // Максимальная длина очереди = 5
        }
    }


    // Метод для получения всех покупателей
    public List<Circle> getCustomers() {
        return customers;
    }
}
