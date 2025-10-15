DROP TABLE IF EXISTS shopping_cart_products, warehouse_product, booked_products;

CREATE TABLE IF NOT EXISTS warehouse_product
(
    product_id UUID PRIMARY KEY,
    quantity   INTEGER,
    fragile    BOOLEAN,
    width      DOUBLE PRECISION NOT NULL,
    height     DOUBLE PRECISION NOT NULL,
    depth      DOUBLE PRECISION NOT NULL,
    weight     DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS booked_products
(
    shopping_cart_id UUID PRIMARY KEY,
    delivery_weight  DOUBLE PRECISION NOT NULL,
    delivery_volume  DOUBLE PRECISION NOT NULL,
    fragile          BOOLEAN          NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart_products
(
    shopping_cart_id UUID PRIMARY KEY,
    product_id       UUID NOT NULL,
    quantity         BIGINT,
    CONSTRAINT fk_shopcart_to_booked FOREIGN KEY(shopping_cart_id) REFERENCES booked_products(shopping_cart_id) ON DELETE CASCADE
);