package org.example.kurs;

import javafx.animation.PauseTransition;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Random;

public abstract class ServicePoint {
    private final Queue<Circle> queue; // Очередь клиентов
    private final int id;             // Идентификатор сервиса
    private boolean isBusy;           // Статус занятости
    private final Random random;      // Генератор случайных чисел
    private final HelloController controller; // Ссылка на контроллер

    public ServicePoint(int id, int maxQueueSize, HelloController controller) {
        this.id = id;
        this.queue = new Queue<>(maxQueueSize); // Использование вашего класса Queue
        this.controller = controller;
        this.isBusy = false;
        this.random = new Random();            // Инициализация генератора случайных чисел
    }

    // Метод для добавления клиента в очередь
    public boolean addCustomer(Circle customer) {
        if (!queue.isFull()) { // Проверяем, есть ли место в очереди
            queue.enqueue(customer); // Добавление клиента в очередь
            System.out.println("Клиент добавлен в очередь сервиса " + id + ". Очередь: " + queue.size());
            return true; // Клиент успешно добавлен в очередь
        }
        System.out.println("Очередь сервиса " + id + " заполнена. Клиент не может быть добавлен.");
        return false; // Если очередь полна, возвращаем false
    }
    public void removeFromQueue(Circle customer) {
        queue.remove(customer);  // Удаляем клиента из очереди
    }

    // Метод для обслуживания следующего клиента
    public void processNextCustomer() {
        if (!queue.isEmpty() && !isBusy) {
            Circle customer = queue.dequeue(); // Извлечение клиента из очереди
            if (customer != null) {
                isBusy = true; // Устанавливаем статус занятости
                int serviceTime = random.nextInt(10) + 1; // Случайное время обслуживания (1-10 секунд)
                System.out.println("Точка обслуживания " + id + " обслуживает клиента в течение " + serviceTime + " секунд. Осталось в очереди: " + queue.size());

                // Используем PauseTransition для имитации времени обслуживания
                PauseTransition pause = new PauseTransition(Duration.seconds(serviceTime));
                pause.setOnFinished(e -> {
                    isBusy = false; // Освобождаем сервис
                    System.out.println("Точка обслуживания " + id + " завершила обслуживание клиента. Очередь: " + queue.size());
                    controller.updateCashTable(id, queue.size());
                    processNextCustomer(); // Переход к следующему клиенту
                });
                pause.play();
            }
        }
    }

    public boolean isBusy() {
        return isBusy; // Статус занятости
    }

    public int getQueueSize() {
        return queue.size(); // Размер очереди
    }

    public int getMaxQueueSize() {
        return queue.getMaxSize(); // Максимальный размер очереди
    }

    public int getId() {
        return id; // Идентификатор сервиса
    }
}

class Consultant extends ServicePoint {
    public Consultant(int id, int maxQueueSize, HelloController controller) {
        super(id, maxQueueSize, controller); // Вызов конструктора родительского класса
    }

    // Метод для обслуживания следующего клиента в очереди
    public void assistCustomer() {
        if (!isBusy()) {
            System.out.println("Консультант " + getId() + " готов обслуживать клиента");
            processNextCustomer(); // Обслуживание клиента
        } else {
            System.out.println("Консультант " + getId() + " занят");
        }
    }
}

class CashRegister extends ServicePoint {
    public CashRegister(int id, int maxQueueSize, HelloController controller) {
        super(id, maxQueueSize, controller); // Вызов конструктора родительского класса
    }

    // Метод для обслуживания клиента
    public void serveCustomer() {
        if (!isBusy()) {
            System.out.println("Касса " + getId() + " готова обслуживать клиента");
            processNextCustomer(); // Обслуживание клиента
        } else {
            System.out.println("Касса " + getId() + " занята");
        }
    }
}
