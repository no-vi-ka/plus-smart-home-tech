package ru.yandex.practicum.analyzer.util;

public class ValueToInteger {
    private ValueToInteger() {

    }

    /**
     * @param value может быть null, boolean или int
     * @return вернёт null, если value - null; вернёт 0/1 если value - Boolean; вернёт Integer, если value Integer
     */
    public static Integer convert(final Object value) {
        switch (value) {
            case null -> {
                return null;
            }

            case Boolean bool -> {
                if (bool) {
                    return 1;
                } else {
                    return 0;
                }
            }

            case Integer integer -> {
                return integer;
            }

            default -> throw new IllegalArgumentException("value может быть null, boolean или int");
        }
    }
}
