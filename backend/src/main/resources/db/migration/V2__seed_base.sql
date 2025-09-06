-- Seed base data for all environments
-- Migration V2: Base seed data

INSERT INTO products (name, description, price, quantity_in_stock) VALUES
('Pen', 'Blue ink pen', 1.50, 100),
('Notebook', 'A5 ruled', 3.20, 200)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO customers (name, contact_info) VALUES
('Alice', 'alice@example.com'),
('Bob', 'bob@example.com')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO suppliers (name, contact_info) VALUES
('Acme Supplies', 'acme@example.com')
ON DUPLICATE KEY UPDATE name = name;
