CREATE TABLE IF NOT EXISTS shopping_cart (
    id UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS shopping_cart_item(
    shopping_cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INT NOT NULL CHECK ( quantity > 0 ),
    PRIMARY KEY (shopping_cart_id, product_id),
    CONSTRAINT fk_shopping_cart_id
        FOREIGN KEY (shopping_cart_id)
        REFERENCES shopping_cart(id) ON DELETE CASCADE
)