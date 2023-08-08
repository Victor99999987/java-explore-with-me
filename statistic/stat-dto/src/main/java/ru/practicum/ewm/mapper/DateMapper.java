package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@UtilityClass
public class DateMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String toStr(@NotNull LocalDateTime date) {
        try {
            return date.format(FORMATTER);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Не удалось перевести дату в строку");
        }
    }

    public LocalDateTime toDate(@NotNull String text) {
        try {
            return LocalDateTime.parse(text, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат строки с датой");
        }
    }
}
