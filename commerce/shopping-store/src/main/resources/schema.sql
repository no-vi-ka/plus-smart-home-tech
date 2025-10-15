DROP TABLE IF EXISTS products;

CREATE TABLE IF NOT EXISTS products
(
    product_id       UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_name     VARCHAR(255) NOT NULL,
    description      VARCHAR(255) NOT NULL,
    image_src        VARCHAR(255),
    quantity_state   VARCHAR(10) NOT NULL,
    product_state    VARCHAR(10) NOT NULL,
    product_category VARCHAR(10) NOT NULL,
    price            REAL
);