-- 1. The Static Catalog Table
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    primary_color VARCHAR(50),
    length_cm INT,
    width_cm INT,
    price DECIMAL(10, 2) NOT NULL
);

-- 2. The Volatile Inventory Table
CREATE TABLE inventory (
    product_id BIGINT PRIMARY KEY,
    stock_quantity INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert Carpet Catalog
INSERT INTO products (id, name, type, primary_color, length_cm, width_cm, price) VALUES
(101, 'Royal Shiraz Persian Rug', 'PERSIAN', 'Crimson Red', 300, 200, 1200.00),
(102, 'Anatolian Geometric Kilim', 'KILIM', 'Terracotta', 250, 150, 450.00),
(103, 'Plush Cloud Shag', 'SHAG', 'Ivory White', 200, 140, 299.99),
(104, 'Vintage Hallway Runner', 'RUNNER', 'Navy Blue', 400, 80, 350.00);

-- Insert Starting Stock
INSERT INTO inventory (product_id, stock_quantity) VALUES
(101, 5),
(102, 15),
(103, 40),
(104, 10);
