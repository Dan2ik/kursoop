package org.example.kurs;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
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
    private CheckBox IsSuper;
    @FXML
    private CheckBox ad;
    @FXML
    private Text CountAll;
    @FXML
    private Text Money, profit;
    @FXML
    private Text salary;
    @FXML
    private Text CountNotServed;
    @FXML
    private TableView<ServiceTableEntry> Cashmatrix; // Таблица для касс
    @FXML
    private TableView<ServiceTableEntry> Consultantmatrix; // Таблица для консультантов

    @FXML
    public Spinner<Integer> MaxQueueCash, MaxQueueCons, CountCash, CountConsultant, MinPeriod, MaxPeriod, discount;

    private Timeline customerGenerator;
    private final Random random = new Random();

    private ObservableList<ServiceTableEntry> cashTableData;
    private ObservableList<ServiceTableEntry> consultantTableData;
    private  Clock clock = new Clock(LocalTime.of(8, 0, 0), DayOfWeek.MONDAY); // Начало в 08:00
    private  Schedule schedule;
    @FXML
    private Label timeLabel;  // Метка для отображения времени

    @FXML
    private Slider speedSlider;  // Слайдер для управления скоростью

    private Habitat habitat;
    private int CountCustomers;
    private int Ad, Discount;
    private int CD=0;
    private int oldcountdays=0;
    private boolean first=true;

    @FXML
    public void initialize() {
        schedule = new Schedule(IsSuper.isSelected());
        Ad = ad.isSelected() ? 1 : 0;
        // Обновляем метку каждую секунд
        // Настраиваем слайдер для управления скоростью
        speedSlider.setMin(0.1);  // Минимальная скорость (0.1x)
        speedSlider.setMax(100.0); // Максимальная скорость (10x)
        speedSlider.setValue(1.0); // По умолчанию 1x
        // Слушатель изменения скорости
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            clock.setSpeed(newValue.doubleValue());
        });

        // Инициализация данных таблиц
        cashTableData = FXCollections.observableArrayList();
        consultantTableData = FXCollections.observableArrayList();

        Cashmatrix.setItems(cashTableData);
        Consultantmatrix.setItems(consultantTableData);
        if (first){
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
            first=false;
        }

        // Настройка Spinner
        discount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        CountCash.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        CountConsultant.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        MinPeriod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 600000, 1));
        MaxPeriod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 600000, 1));
        MaxQueueCons.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        MaxQueueCash.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));


        // Создание таймера для генерации покупателей

        customerGenerator = new Timeline(
                new KeyFrame(Duration.seconds(getRandomPeriod()), e -> {
                    if (schedule.isOpen(clock.getCurrentDay().getValue(), clock.getHour().getHour())) {
                        CountCustomers++;
                        CountAll.setText(String.valueOf(CountCustomers));
                        habitat.generateCustomer();
                    }
                    restartCustomerGenerator(); // Перезапускаем генератор с новым интервалом
                })
        );
        customerGenerator.setCycleCount(1); // Устанавливаем один цикл для текущего интервала
        customerGenerator.play();
        customerGenerator.stop();
    }


    // Запуск симуляции
    public void start(ActionEvent actionEvent) {
        if (schedule.isOpen(clock.getCurrentDay().getValue(), clock.getHour().getHour())) {
            clock.start();
            Timeline updateLabelTimeline = new Timeline(new KeyFrame(Duration.millis(100/speedSlider.getValue()), e -> {
                System.out.println(clock.getCurrentTime());
                if (clock.getCountDays() != oldcountdays) {
                    CD += 1500 * (CountCash.getValue() + CountConsultant.getValue());
                    salary.setText(String.valueOf(CD));
                    if (ad.isSelected()) {
                        CD -= 7000;
                    }
                    String moneyText = Money.getText();
                    String moneyValueStr = moneyText.replaceAll("[^0-9.]", ""); // Удаление всех нечисловых символов
                    double moneyValue = Double.parseDouble(moneyValueStr);
                    double profitValue = (moneyValue / 100) * 9 - CD;
                    profit.setText(String.valueOf(profitValue));

                    oldcountdays = clock.getCountDays();
                }
                String timeText = clock.getCurrentTime();
                String dayText = clock.getCurrentDay().toString();
                timeLabel.setText("Время: " + timeText + " | День недели: " + dayText);
            }));
            updateLabelTimeline.setCycleCount(Timeline.INDEFINITE);
            updateLabelTimeline.play();
            // Инициализация Habitat
            habitat = new Habitat(pane, centre, entry, consultant, cash, exit, CountCash.getValue(), CountConsultant.getValue(), MaxQueueCash.getValue(), MaxQueueCons.getValue(), this);
            customerGenerator.play();
        }
    }

    public int workers(){
        return CountCash.getValue()+ CountConsultant.getValue();
    }
    // Остановка симуляции
    public void stop(ActionEvent actionEvent) {
        clock.stop();
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
        double a;
        a = (((min + (max - min) * random.nextDouble()) / speedSlider.getValue()) + 0.1 * Ad * ((min + (max - min) * random.nextDouble()) / speedSlider.getValue()) + 0.005 * Discount * ((min + (max - min) * random.nextDouble()) / speedSlider.getValue()));

        return a;
    }

    public void updateSimulationParameters(MouseEvent mouseEvent) {
    }
    // Метод сброса приложения до начального состояния
    public void reset(ActionEvent actionEvent) {
        // Остановка симуляции и таймера
        if (clock != null) {
            clock.stop();
        }
        if (customerGenerator != null) {
            customerGenerator.stop();
        }

        // Сброс текстовых меток
        CountAll.setText("0");
        Money.setText("0");
        profit.setText("0");
        salary.setText("0");
        CountNotServed.setText("0");
        timeLabel.setText("Время: 08:00 | День недели: MONDAY");

        // Сброс слайдера скорости
        speedSlider.setValue(1.0);

        // Сброс спиннеров к их начальному состоянию
        discount.getValueFactory().setValue(0);
        CountCash.getValueFactory().setValue(1);
        CountConsultant.getValueFactory().setValue(1);
        MinPeriod.getValueFactory().setValue(1);
        MaxPeriod.getValueFactory().setValue(2);
        MaxQueueCash.getValueFactory().setValue(1);
        MaxQueueCons.getValueFactory().setValue(1);

        // Очистка таблиц
        cashTableData.clear();
        consultantTableData.clear();

        // Очистка других переменных
        CountCustomers = 0;
        Ad = 0;
        Discount = 0;
        CD = 0;
        oldcountdays = 0;
        // Сброс параметров Habitat и Schedule
        habitat = null;
        schedule = new Schedule(IsSuper.isSelected());

        // Инициализация часов и таймера заново
        clock = new Clock(LocalTime.of(8, 0, 0), DayOfWeek.MONDAY);
        initialize(); // Перезапуск начальных настроек
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
    public void updateMoney(String s){
        Money.setText(s);
    }
    public int getDiscount(){
        return discount.getValue();
    }
}
