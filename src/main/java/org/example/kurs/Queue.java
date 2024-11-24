package org.example.kurs;
import java.util.LinkedList;

public class Queue<T> {
    private LinkedList<T> elements;
    private int maxSize;

    // Конструктор с максимальной длиной
    public Queue(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be greater than 0");
        }
        this.elements = new LinkedList<>();
        this.maxSize = maxSize;
    }

    // Добавление элемента в конец очереди
    public void enqueue(T item) {
        if (elements.size() >= maxSize) {
            throw new IllegalStateException("Queue is full");
        }
        elements.addLast(item);
    }

    // Удаление элемента из начала очереди
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return elements.removeFirst();
    }

    // Просмотр первого элемента без удаления
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return elements.getFirst();
    }

    // Проверка, пуста ли очередь
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    // Проверка, полна ли очередь
    public boolean isFull() {
        return elements.size() >= maxSize;
    }

    // Получение размера очереди
    public int size() {
        return elements.size();
    }

    // Очистка очереди
    public void clear() {
        elements.clear();
    }

    // Получение максимального размера очереди
    public int getMaxSize() {
        return maxSize;
    }

    // Печать содержимого очереди
    @Override
    public String toString() {
        return elements.toString();
    }
}