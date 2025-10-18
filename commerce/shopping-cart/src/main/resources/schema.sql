DROP TABLE IF EXISTS shopping_cart, shopping_cart_items;

CREATE TABLE IF NOT EXISTS shopping_cart
(
    shopping_cart_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart_items
(
    product_id UUID NOT NULL,
    quantity INTEGER,
    cart_id uuid REFERENCES shopping_cart (shopping_cart_id) ON DELETE CASCADE
);