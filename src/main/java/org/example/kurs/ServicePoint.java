package org.example.kurs;

import javafx.animation.PauseTransition;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public abstract class ServicePoint {
    private final Queue<Circle> queue;  // Очередь клиентов
    private final int maxQueueSize;     // Максимальная длина очереди
    private final int id;               // Идентификатор
    private boolean isBusy;             // Статус занятости
    private final Random random;        // Генератор случайных чисел

    public ServicePoint(int id, int maxQueueSize) {
        this.id = id;
        this.maxQueueSize = maxQueueSize;
        this.queue = new LinkedList<>(); // Инициализация очереди с использованием LinkedList
        this.isBusy = false;
        this.random = new Random();      // Инициализация генератора случайных чисел
    }

    // Метод для добавления клиента в очередь
    public boolean addCustomer(Circle customer) {
        if (queue.size() < maxQueueSize) { // Проверяем, есть ли место в очереди
            queue.add(customer);  // Добавление клиента в очередь
            return true;  // Клиент успешно добавлен в очередь
        }
        return false;  // Если очередь полна, возвращаем false
    }

    // Метод для обслуживания следующего клиента
    public void processNextCustomer() {
        if (!queue.isEmpty() && !isBusy) {
            Circle customer = queue.poll(); // Извлечение клиента из очереди
            if (customer != null) {
                isBusy = true; // Устанавливаем статус занятости
                int serviceTime = random.nextInt(5) + 1; // Случайное время обслуживания (1-5 секунд)
                System.out.println("Сервис " + id + " обслуживает клиента в течение " + serviceTime + " секунд");

                // Используем PauseTransition для имитации времени обслуживания
                PauseTransition pause = new PauseTransition(Duration.seconds(serviceTime));
                pause.setOnFinished(e -> {
                    isBusy = false; // Освобождаем сервис
                    System.out.println("Сервис " + id + " завершил обслуживание клиента");
                    processNextCustomer(); // Переход к следующему клиенту
                });
                pause.play();
            }
        }
    }

    public boolean isBusy() {
        return isBusy;  // Статус занятости
    }

    public int getQueueSize() {
        return queue.size();  // Размер очереди
    }

    public int getMaxQueueSize() {
        return maxQueueSize;  // Максимальный размер очереди
    }

    public int getId() {
        return id;  // Идентификатор сервиса
    }
}

class Consultant extends ServicePoint {
    public Consultant(int id, int maxQueueSize) {
        super(id, maxQueueSize);  // Вызов конструктора родительского класса
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
    public CashRegister(int id, int maxQueueSize) {
        super(id, maxQueueSize);  // Вызов конструктора родительского класса
    }

    // Метод для обслуживания клиента (например, клиент покидает кассу после обслуживания)
    public void serveCustomer() {
        if (!isBusy()) {
            System.out.println("Касса " + getId() + " готова обслуживать клиента");
            processNextCustomer(); // Обслуживание клиента
        } else {
            System.out.println("Касса " + getId() + " занята");
        }
    }
}

