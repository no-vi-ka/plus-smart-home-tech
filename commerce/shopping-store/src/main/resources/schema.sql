CREATE TABLE IF NOT EXISTS products
(
    product_id     uuid DEFAULT gen_random_uuid() NOT NULL,
    product_name   VARCHAR(255) NOT NULL,
    description    VARCHAR(500) NOT NULL,
    image_src      VARCHAR(255),
    quantity_state VARCHAR(10) NOT NULL,
    product_state  VARCHAR(10) NOT NULL,
    product_category VARCHAR(10) NOT NULL,
    price NUMERIC(19,2) NOT NULL,

    CONSTRAINT products_pk PRIMARY KEY (product_id),

    CONSTRAINT check_quantity_state
        CHECK (quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    CONSTRAINT check_product_state
        CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),
    CONSTRAINT check_product_category
        CHECK (product_category IN ('LIGHTING', 'CONTROL', 'SENSORS'))

);