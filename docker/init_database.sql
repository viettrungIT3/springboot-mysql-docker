-- Schema initialization for MySQL

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT NOT NULL
);

CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name TEXT NOT NULL,
    contact_info TEXT
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name TEXT NOT NULL,
    contact_info TEXT
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    order_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS stock_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT,
    supplier_id BIGINT,
    quantity INT NOT NULL,
    entry_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_entries_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stock_entries_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE IF NOT EXISTS administrators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name TEXT
);

-- Seed minimal data
INSERT INTO products (name, description, price, quantity_in_stock) VALUES
('Pen', 'Blue ink pen', 1.50, 100),
('Notebook', 'A5 ruled', 3.20, 200);

INSERT INTO customers (name, contact_info) VALUES
('Alice', 'alice@example.com'),
('Bob', 'bob@example.com');

INSERT INTO suppliers (name, contact_info) VALUES
('Acme Supplies', 'acme@example.com');
