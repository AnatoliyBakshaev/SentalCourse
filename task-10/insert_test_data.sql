DROP TABLE IF EXISTS Printer CASCADE;
DROP TABLE IF EXISTS Laptop CASCADE;
DROP TABLE IF EXISTS PC CASCADE;
DROP TABLE IF EXISTS Product CASCADE;

-- ============================================
-- 2. СОЗДАНИЕ ТАБЛИЦ
-- ============================================

CREATE TABLE Product (
    maker VARCHAR(10) NOT NULL,
    model VARCHAR(50) PRIMARY KEY,
    type VARCHAR(50) NOT NULL CHECK (type IN ('PC', 'Laptop', 'Printer'))
);

CREATE TABLE PC (
    code INT PRIMARY KEY,
    model VARCHAR(50) NOT NULL REFERENCES Product(model) ON DELETE CASCADE,
    speed SMALLINT NOT NULL CHECK (speed > 0),
    ram INTEGER NOT NULL CHECK (ram > 0),
    hd REAL NOT NULL CHECK (hd > 0),
    cd VARCHAR(10) NOT NULL,
    price MONEY NOT NULL CHECK (price > 0::money)
);

CREATE TABLE Laptop (
    code INT PRIMARY KEY,
    model VARCHAR(50) NOT NULL REFERENCES Product(model) ON DELETE CASCADE,
    speed SMALLINT NOT NULL CHECK (speed > 0),
    ram INTEGER NOT NULL CHECK (ram > 0),
    hd REAL NOT NULL CHECK (hd > 0),
    screen SMALLINT NOT NULL CHECK (screen > 0),
    price MONEY NOT NULL CHECK (price > 0::money)
);

CREATE TABLE Printer (
    code INT PRIMARY KEY,
    model VARCHAR(50) NOT NULL REFERENCES Product(model) ON DELETE CASCADE,
    color CHAR(1) NOT NULL CHECK (color IN ('y', 'n')),
    type VARCHAR(10) NOT NULL CHECK (type IN ('Laser', 'Jet', 'Matrix')),
    price MONEY NOT NULL CHECK (price > 0::money)
);

CREATE INDEX idx_pc_model ON PC(model);
CREATE INDEX idx_laptop_model ON Laptop(model);
CREATE INDEX idx_printer_model ON Printer(model);
CREATE INDEX idx_product_type ON Product(type);
CREATE INDEX idx_product_maker ON Product(maker);

-- ============================================
-- 3. ЗАПОЛНЕНИЕ PRODUCT (все модели)
-- ============================================
INSERT INTO Product (maker, model, type) VALUES
-- Производитель A
('A', 'PC-001', 'PC'),
('A', 'PC-002', 'PC'),
('A', 'PC-003', 'PC'),
('A', 'PC-004', 'PC'),
('A', 'L-001', 'Laptop'),
('A', 'L-002', 'Laptop'),
('A', 'L-003', 'Laptop'),

-- Производитель B
('B', 'PC-005', 'PC'),
('B', 'PC-006', 'PC'),
('B', 'L-004', 'Laptop'),
('B', 'L-005', 'Laptop'),
('B', 'P-001', 'Printer'),
('B', 'P-002', 'Printer'),
('B', 'P-003', 'Printer'),

-- Производитель C
('C', 'PC-007', 'PC'),
('C', 'PC-008', 'PC'),
('C', 'PC-009', 'PC'),
('C', 'L-006', 'Laptop'),
('C', 'L-007', 'Laptop'),
('C', 'P-004', 'Printer'),
('C', 'P-005', 'Printer'),

-- Производитель D
('D', 'PC-010', 'PC'),
('D', 'PC-011', 'PC'),
('D', 'PC-012', 'PC'),
('D', 'PC-013', 'PC'),  -- ← ДОБАВИЛ!

-- Производитель E
('E', 'L-008', 'Laptop'),
('E', 'L-009', 'Laptop'),
('E', 'P-006', 'Printer'),
('E', 'P-007', 'Printer'),

-- Дополнительные модели для PC (чтобы не было ошибок)
('A', 'PC-014', 'PC'),
('A', 'PC-015', 'PC'),
('A', 'PC-016', 'PC'),
('A', 'PC-017', 'PC'),
('A', 'PC-018', 'PC'),
('A', 'PC-019', 'PC'),
('A', 'PC-020', 'PC'),
('A', 'PC-021', 'PC'),
('A', 'PC-022', 'PC'),
('A', 'PC-023', 'PC'),
('A', 'PC-024', 'PC'),
('A', 'PC-025', 'PC'),
('A', 'PC-026', 'PC'),
('A', 'PC-027', 'PC'),
('A', 'PC-028', 'PC'),

-- Дополнительные модели для Laptop
('A', 'L-010', 'Laptop'),
('A', 'L-011', 'Laptop'),
('A', 'L-012', 'Laptop'),
('A', 'L-013', 'Laptop'),
('A', 'L-014', 'Laptop'),
('A', 'L-015', 'Laptop'),
('A', 'L-016', 'Laptop'),
('A', 'L-017', 'Laptop'),
('A', 'L-018', 'Laptop'),
('A', 'L-019', 'Laptop'),

-- Дополнительные модели для Printer
('A', 'P-008', 'Printer'),
('A', 'P-009', 'Printer'),
('A', 'P-010', 'Printer'),
('A', 'P-011', 'Printer'),
('A', 'P-012', 'Printer');

-- ============================================
-- 4. ЗАПОЛНЕНИЕ PC
-- ============================================
INSERT INTO PC (code, model, speed, ram, hd, cd, price) VALUES
-- Запрос 1: ПК < $500
(1, 'PC-001', 1800, 2048, 250, '12x', 449.99),
(2, 'PC-002', 2000, 4096, 320, '16x', 499.99),

-- Запрос 5: 12x или 24x и < $600
(3, 'PC-003', 2200, 4096, 500, '12x', 549.99),
(4, 'PC-004', 2400, 8192, 750, '24x', 599.99),

-- Производитель A (запрос 13)
(5, 'PC-005', 2800, 8192, 1000, '16x', 899.99),
(6, 'PC-006', 3200, 16384, 2000, '24x', 1299.99),

-- Производитель B (запрос 9 - скорость >= 450)
(7, 'PC-007', 3500, 16384, 2000, '48x', 1999.99),
(8, 'PC-008', 3000, 8192, 1024, '24x', 1399.99),

-- Производитель C (запрос 20 - минимум 3 модели)
(9, 'PC-009', 2600, 4096, 500, '16x', 799.99),
(10, 'PC-010', 2900, 8192, 750, '24x', 1099.99),
(11, 'PC-011', 3400, 12288, 1500, '32x', 1799.99),

-- Производитель D (запрос 8 - только ПК)
(12, 'PC-012', 2100, 4096, 320, '12x', 649.99),
(13, 'PC-013', 2500, 8192, 500, '16x', 849.99),  -- ← ТЕПЕРЬ ЕСТЬ В PRODUCT

-- Запрос 16: Пары с одинаковой скоростью и RAM
(14, 'PC-014', 2800, 8192, 1000, '24x', 999.99),
(15, 'PC-015', 2800, 8192, 1200, '24x', 1099.99),

-- Запрос 15: Повторяющиеся hd (500 GB)
(16, 'PC-016', 3000, 8192, 500, '16x', 1199.99),

-- Запрос 25: Минимальный RAM (1024)
(17, 'PC-017', 1500, 1024, 160, '8x', 399.99),
(18, 'PC-018', 1600, 1024, 200, '12x', 429.99),

-- Запрос 17: ПК с низкой скоростью
(19, 'PC-019', 1100, 2048, 250, '8x', 349.99),

-- Запрос 22: speed > 600
(20, 'PC-020', 800, 1024, 160, '4x', 299.99),
(21, 'PC-021', 900, 2048, 200, '8x', 329.99),
(22, 'PC-022', 1000, 4096, 320, '12x', 379.99),
(23, 'PC-023', 1200, 4096, 500, '16x', 429.99),
(24, 'PC-024', 1400, 8192, 750, '24x', 499.99),

-- Дополнительные
(25, 'PC-025', 3600, 16384, 2000, '48x', 2199.99),
(26, 'PC-026', 3800, 24576, 4000, '64x', 2899.99),
(27, 'PC-027', 4200, 32767, 4000, '72x', 3499.99),
(28, 'PC-028', 4500, 24576, 8000, '96x', 4999.99);

-- ============================================
-- 5. ЗАПОЛНЕНИЕ LAPTOP
-- ============================================
INSERT INTO Laptop (code, model, speed, ram, hd, screen, price) VALUES
-- Запрос 3: Цена > $1000
(101, 'L-001', 2100, 4096, 512, 14, 1099.99),
(102, 'L-002', 2400, 8192, 1024, 15, 1299.99),

-- Запрос 6: hd >= 100 ГБ
(103, 'L-003', 1800, 2048, 256, 13, 699.99),
(104, 'L-004', 2000, 4096, 512, 14, 899.99),

-- Производитель A (запрос 19)
(105, 'L-005', 2600, 16384, 2048, 17, 1899.99),
(106, 'L-006', 1900, 4096, 500, 13, 849.99),

-- Производитель B (запрос 23 - скорость >= 750)
(107, 'L-007', 2800, 8192, 1024, 15, 1499.99),
(108, 'L-008', 2200, 4096, 500, 14, 999.99),

-- Производитель C (запрос 19)
(109, 'L-009', 1600, 2048, 256, 11, 599.99),
(110, 'L-010', 2300, 8192, 750, 15, 1199.99),

-- Производитель E (запрос 17 - скорость меньше любого ПК)
(111, 'L-011', 900, 2048, 320, 12, 499.99),
(112, 'L-012', 1000, 2048, 250, 11, 449.99),

-- Запрос 12: Средняя скорость ноутбуков > $1000
(113, 'L-013', 2700, 16384, 1024, 16, 1599.99),
(114, 'L-014', 2500, 8192, 512, 14, 1099.99),

-- Дополнительные
(115, 'L-015', 1500, 4096, 500, 13, 749.99),
(116, 'L-016', 1700, 8192, 512, 14, 849.99),
(117, 'L-017', 1900, 8192, 1024, 15, 999.99),
(118, 'L-018', 2100, 16384, 2048, 16, 1399.99),
(119, 'L-019', 2300, 16384, 2048, 17, 1699.99);

-- ============================================
-- 6. ЗАПОЛНЕНИЕ PRINTER
-- ============================================
INSERT INTO Printer (code, model, color, type, price) VALUES
-- Запрос 4: Цветные принтеры
(201, 'P-001', 'y', 'Laser', 299.99),
(202, 'P-002', 'y', 'Jet', 149.99),

-- Запрос 10: Самая высокая цена
(203, 'P-003', 'y', 'Laser', 499.99),

-- Запрос 18: Самые дешевые цветные принтеры
(204, 'P-004', 'y', 'Jet', 99.99),
(205, 'P-005', 'y', 'Matrix', 99.99),

-- Черно-белые принтеры
(206, 'P-006', 'n', 'Laser', 349.99),
(207, 'P-007', 'n', 'Jet', 79.99),

-- Производитель C (запрос 18)
(208, 'P-008', 'y', 'Jet', 99.99),

-- Дополнительные
(209, 'P-009', 'n', 'Laser', 249.99),
(210, 'P-010', 'y', 'Laser', 399.99),
(211, 'P-011', 'n', 'Matrix', 149.99),
(212, 'P-012', 'y', 'Matrix', 199.99);

-- ============================================
-- 7. ПРОВЕРКА
-- ============================================
SELECT 'Product' AS table_name, COUNT(*) AS rows FROM Product
UNION ALL
SELECT 'PC', COUNT(*) FROM PC
UNION ALL
SELECT 'Laptop', COUNT(*) FROM Laptop
UNION ALL
SELECT 'Printer', COUNT(*) FROM Printer;

-- Проверка внешних ключей (должно быть 0)
SELECT 'PC orphans' AS check_name, COUNT(*) AS count 
FROM PC WHERE model NOT IN (SELECT model FROM Product)
UNION ALL
SELECT 'Laptop orphans', COUNT(*) 
FROM Laptop WHERE model NOT IN (SELECT model FROM Product)
UNION ALL
SELECT 'Printer orphans', COUNT(*) 
FROM Printer WHERE model NOT IN (SELECT model FROM Product);