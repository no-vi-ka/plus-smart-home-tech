DROP TABLE IF EXISTS shopping_cart, shopping_cart_products CASCADE;

CREATE TABLE IF NOT EXISTS shopping_cart
(
    shopping_cart_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username         VARCHAR(255) NOT NULL,
    active           BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart_products
(
    product_id       UUID NOT NULL,
    quantity         BIGINT,
    shopping_cart_id UUID,
    CONSTRAINT fk_shopcart_to_carts FOREIGN KEY(shopping_cart_id) REFERENCES shopping_cart(shopping_cart_id) ON DELETE CASCADE
);