package org.example.kurs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Random;

public class HelloController {

    // Координаты элементов интерфейса
    double entryx, entryy, centrex, centrey, consultantx, consultanty, cashx, cashy, exitx, exity;

    @FXML
    private Circle entry, centre, consultant, cash, exit;

    @FXML
    private Pane pane;
    @FXML
    private Text CountAll;
    @FXML
    private Text CountNotServed;
    @FXML
    private TableView<ServiceTableEntry> Cashmatrix; // Таблица для касс
    @FXML
    private TableView<ServiceTableEntry> Consultantmatrix; // Таблица для консультантов

    @FXML
    private Spinner<Integer> MaxQueueCash, MaxQueueCons, CountCash, CountConsultant, MinPeriod, MaxPeriod;

    private Timeline customerGenerator;
    private final Random random = new Random();

    private ObservableList<ServiceTableEntry> cashTableData;
    private ObservableList<ServiceTableEntry> consultantTableData;
    private  Clock clock = new Clock(LocalTime.of(8, 0, 0), DayOfWeek.MONDAY); // Начало в 08:00
    private final Schedule schedule= new Schedule(false);
    @FXML
    private Label timeLabel;  // Метка для отображения времени

    @FXML
    private Slider speedSlider;  // Слайдер для управления скоростью

    private Habitat habitat;
    private int CountCustomers;

    @FXML
    public void initialize() {

        // Обновляем метку каждую секунд
        // Настраиваем слайдер для управления скоростью
        speedSlider.setMin(0.1);  // Минимальная скорость (0.1x)
        speedSlider.setMax(100.0); // Максимальная скорость (10x)
        speedSlider.setValue(1.0); // По умолчанию 1x
        // Слушатель изменения скорости
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            clock.setSpeed(newValue.doubleValue());
        });

        // Запускаем часы
        clock.start();
        // Инициализация данных таблиц
        cashTableData = FXCollections.observableArrayList();
        consultantTableData = FXCollections.observableArrayList();

        Cashmatrix.setItems(cashTableData);
        Consultantmatrix.setItems(consultantTableData);

        // Настройка таблицы для касс
        TableColumn<ServiceTableEntry, Integer> cashIdColumn = new TableColumn<>("Номер кассы");
        cashIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));

        TableColumn<ServiceTableEntry, Integer> cashQueueColumn = new TableColumn<>("Очередь");
        cashQueueColumn.setCellValueFactory(new PropertyValueFactory<>("queueSize"));

        Cashmatrix.getColumns().addAll(cashIdColumn, cashQueueColumn);

        // Настройка таблицы для консультантов
        TableColumn<ServiceTableEntry, Integer> consultantIdColumn = new TableColumn<>("Номер консультанта");
        consultantIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));

        TableColumn<ServiceTableEntry, Integer> consultantQueueColumn = new TableColumn<>("Очередь");
        consultantQueueColumn.setCellValueFactory(new PropertyValueFactory<>("queueSize"));

        Consultantmatrix.getColumns().addAll(consultantIdColumn, consultantQueueColumn);

        // Настройка Spinner
        CountCash.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        CountConsultant.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        MinPeriod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1));
        MaxPeriod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 60, 1));
        MaxQueueCons.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        MaxQueueCash.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));



        // Создание таймера для генерации покупателей
        customerGenerator = new Timeline(
                new KeyFrame(Duration.seconds(getRandomPeriod()/speedSlider.getValue()), e -> {
                    CountCustomers++;
                    CountAll.setText(String.valueOf(CountCustomers));
                    habitat.generateCustomer();

                })
        );
        customerGenerator.setCycleCount(Timeline.INDEFINITE);
    }

    // Запуск симуляции
    public void start(ActionEvent actionEvent) {
        if(schedule.isOpen(clock.getCurrentDay().getValue(), clock.getHour().getHour())){
        Timeline updateLabelTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            timeLabel.setText(clock.getCurrentTime());
        }));
        updateLabelTimeline.setCycleCount(Timeline.INDEFINITE);
        updateLabelTimeline.play();
        // Инициализация Habitat
        habitat = new Habitat(pane, centre, entry, consultant, cash, exit, CountCash.getValue(), CountConsultant.getValue(), MaxQueueCash.getValue(), MaxQueueCons.getValue(), this);
        customerGenerator.play();
        }
    }

    // Остановка симуляции
    public void stop(ActionEvent actionEvent) {
        customerGenerator.stop();
    }

    // Перезапуск таймера с новыми параметрами
    private void restartCustomerGenerator() {
        customerGenerator.stop();
        customerGenerator = new Timeline(
                new KeyFrame(Duration.seconds(getRandomPeriod()), e -> {

                        habitat.generateCustomer();

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

    public void updateSimulationParameters(MouseEvent mouseEvent) {
    }

    // Класс записи для таблицы
    public static class ServiceTableEntry {
        private final IntegerProperty serviceId;
        private final IntegerProperty queueSize;

        public ServiceTableEntry(int serviceId, int queueSize) {
            this.serviceId = new SimpleIntegerProperty(serviceId);
            this.queueSize = new SimpleIntegerProperty(queueSize);
        }

        public int getServiceId() {
            return serviceId.get();
        }

        public IntegerProperty serviceIdProperty() {
            return serviceId;
        }

        public int getQueueSize() {
            return queueSize.get();
        }

        public IntegerProperty queueSizeProperty() {
            return queueSize;
        }
    }

    public void updateNotServed(int a) {
        CountNotServed.setText(String.valueOf(a));
    }
    // Обновление таблицы касс
    public void updateCashTable(int cashId, int queueSize) {
        for (ServiceTableEntry entry : cashTableData) {
            if (entry.getServiceId() == cashId) {
                entry.queueSizeProperty().set(queueSize);
                return;
            }
        }
        cashTableData.add(new ServiceTableEntry(cashId, queueSize));
    }

    // Обновление таблицы консультантов
    public void updateConsultantTable(int consultantId, int queueSize) {
        for (ServiceTableEntry entry : consultantTableData) {
            if (entry.getServiceId() == consultantId) {
                entry.queueSizeProperty().set(queueSize);
                return;
            }
        }
        consultantTableData.add(new ServiceTableEntry(consultantId, queueSize));
    }
}
