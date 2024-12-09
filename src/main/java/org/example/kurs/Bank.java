package org.example.kurs;

public class Bank {

    private double budget;

    // Конструктор по умолчанию
    public Bank() {
        this.budget = 0.0;
    }

    // Конструктор с параметром
    public Bank(double initialBudget) {
        if (initialBudget < 0) {
            throw new IllegalArgumentException("Бюджет не может быть отрицательным.");
        }
        this.budget = initialBudget;
    }

    // Геттер для бюджета
    public double getBudget() {
        return budget;
    }

    // Сеттер для бюджета
    public void setBudget(double budget) {
        if (budget < 0) {
            throw new IllegalArgumentException("Бюджет не может быть отрицательным.");
        }
        this.budget = budget;
    }

    // Метод для добавления суммы к бюджету
    public void deposit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма для депозита не может быть отрицательной.");
        }
        this.budget += amount;
    }

    // Метод для снятия суммы с бюджета
    public void withdraw(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма для снятия не может быть отрицательной.");
        }
        if (amount > this.budget) {
            throw new IllegalArgumentException("Недостаточно средств на счете.");
        }
        this.budget -= amount;
    }

    @Override
    public String toString() {
        return String.valueOf(budget);
    }

}
