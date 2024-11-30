package org.example.kurs;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Timer;

import java.util.Random;

public class HelloController {

    double entryx, entryy, centrex, centrey, consultantx, consultanty, cashx, cashy, exitx, exity, customerx, customery;
    @FXML
    private Circle customer;
    @FXML
    private Circle entry;
    @FXML
    private Circle centre;
    @FXML
    private Circle consultant;
    @FXML
    private Circle cash;
    @FXML
    private Circle exit;
    private Habitat habitat;
    @FXML
    private Pane pane;
    @FXML
    private TableView Cashmatrix;
    @FXML
    private TableView Consultantmatrix;
    @FXML
    private Spinner<Integer> MaxQueueCash;       // Количество касс
    @FXML
    private Spinner<Integer> MaxQueueCons; // Количество консультантов
    @FXML
    private Spinner<Integer> CountCash;       // Количество касс
    @FXML
    private Spinner<Integer> CountConsultant; // Количество консультантов
    @FXML
    private Spinner<Integer> MinPeriod;      // Минимальный период генерации
    @FXML
    private Spinner<Integer> MaxPeriod;      // Максимальный период генерации

    private Timeline customerGenerator;
    private final Random random = new Random();
    @FXML
    public void initialize() {
        entryx = entry.getLayoutX();
        entryy = entry.getLayoutY();
        centrex = centre.getLayoutX();
        centrey = centre.getLayoutY();
        consultantx = consultant.getLayoutX();
        consultanty = consultant.getLayoutY();
        cashx = cash.getLayoutX();
        cashy = cash.getLayoutY();
        exitx = exit.getLayoutX();
        exity = exit.getLayoutY();
        CountCash.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1)); // от 1 до 10 с начальным значением 1
        CountConsultant.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        MinPeriod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1));
        MaxPeriod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 60, 1));


        // Инициализация Habitat
        habitat = new Habitat(pane, centre,entry, consultant, cash, exit,CountCash.getValue(), CountConsultant.getValue());

    // Создаем таймер для генерации покупателей
        customerGenerator = new Timeline(
                new KeyFrame(Duration.seconds(getRandomPeriod()), e -> {
                    try {
                        habitat.generateCustomer();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                })
        );
        customerGenerator.setCycleCount(Timeline.INDEFINITE); // Таймер бесконечный
    }

    // Запуск симуляции
    public void start(ActionEvent actionEvent) {
        customerGenerator.play(); // Запуск генерации
    }

    // Остановка симуляции
    public void stop(ActionEvent actionEvent) {
        customerGenerator.stop(); // Остановка генерации
    }
    // Старт симуляции
    public void startSimulation() {
        customerGenerator.play();
    }

    // Остановка симуляции
    public void stopSimulation() {
        customerGenerator.stop();
    }

    // Метод для обновления таймера при изменении значений Spinner
    public void updateSimulationParameters() {
        restartCustomerGenerator();
    }

    // Перезапуск таймера с новыми параметрами
    private void restartCustomerGenerator() {
        if (customerGenerator != null) {
            customerGenerator.stop();
        }
        customerGenerator = new Timeline(
                new KeyFrame(Duration.seconds(getRandomPeriod()), e -> {
                    try {
                        habitat.generateCustomer();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                })
        );
        customerGenerator.setCycleCount(Timeline.INDEFINITE);
        customerGenerator.play();
    }

    // Генерация случайного периода от MinPeriod до MaxPeriod
    private double getRandomPeriod() {
        int min = MinPeriod.getValue();
        int max = MaxPeriod.getValue();
        return min + (max - min) * random.nextDouble();
    }
}
