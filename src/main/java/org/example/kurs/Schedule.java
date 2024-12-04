package org.example.kurs;

public class Schedule {
    private final boolean isSupermarket;

    public Schedule(boolean isSupermarket) {
        this.isSupermarket = isSupermarket;
    }

    /**
     * Проверяет, открыт ли магазин в текущее время симуляции.
     *
     * @return true, если магазин открыт, иначе false.
     */
    public boolean isOpen(int currentDay, int currentHour) {
        if (isSupermarket) {
            return true; // Супермаркет всегда открыт.
        }

        // Получаем текущий день недели и час из симуляции через Clock
        // 1 - Понедельник, 7 - Воскресенье

        // Проверяем часы работы для магазина
        switch (currentDay) {
            case 1: // Понедельник
                return currentHour >= 8 && currentHour < 19; // 11 рабочих часов (8:00 - 19:00).
            case 2: // Вторник
                return currentHour >= 8 && currentHour < 19; // 11 рабочих часов (8:00 - 19:00).
            case 3: // Среда
                return currentHour >= 8 && currentHour < 19; // 11 рабочих часов (8:00 - 19:00).
            case 4: // Четверг
                return currentHour >= 8 && currentHour < 19; // 11 рабочих часов (8:00 - 19:00).
            case 5: // Пятница
                return currentHour >= 8 && currentHour < 19; // 11 рабочих часов (8:00 - 19:00).
            case 6: // Суббота
                return currentHour >= 9 && currentHour < 18; // 9 рабочих часов (9:00 - 18:00).
            case 7: // Воскресенье
                return currentHour >= 10 && currentHour < 17; // 7 рабочих часов (10:00 - 17:00).
            default:
                return false; // На случай ошибки.
        }
    }
}