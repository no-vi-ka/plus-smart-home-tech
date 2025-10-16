package ru.yandex.practicum.shopping.cart.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsernameValidator {

    public static boolean isUsernameValid(String username) {
        return username != null && !username.isBlank();
    }

}
