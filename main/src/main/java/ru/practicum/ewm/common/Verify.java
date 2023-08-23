package ru.practicum.ewm.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Verify {
    public static void verifyFromAndSize(int from, int size) {
        if (from < 0) {
            throw new IllegalArgumentException("Минимальное значение записи, с которой можно получить данные равно 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Количество записей на странице должно быть больше 0");
        }
    }
}
