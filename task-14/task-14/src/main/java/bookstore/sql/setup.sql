-- =============================================
-- ПОЛНЫЙ ФАЙЛ УСТАНОВКИ БАЗЫ ДАННЫХ
-- =============================================

-- Создание базы данных (раскомментировать при необходимости)
-- CREATE DATABASE bookstore
--     WITH 
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'ru_RU.UTF-8'
--     LC_CTYPE = 'ru_RU.UTF-8'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1;

-- Подключение к базе данных
-- \c bookstore;

-- =============================================
-- 1. СОЗДАНИЕ ТАБЛИЦ
-- =============================================

-- Таблица книг
CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_STOCK',
    quantity INTEGER NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    publication_date DATE,
    received_date DATE,
    description TEXT,
    genre VARCHAR(100),
    publisher VARCHAR(255),
    pages INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для books
CREATE INDEX IF NOT EXISTS idx_books_status ON books(status);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);

-- Таблица заказов
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    book_id INTEGER NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20),
    customer_email VARCHAR(255),
    customer_address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_date TIMESTAMP NULL,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_book FOREIGN KEY (book_id) 
        REFERENCES books(id) ON DELETE RESTRICT
);

-- Индексы для orders
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date);
CREATE INDEX IF NOT EXISTS idx_orders_customer ON orders(customer_name);
CREATE INDEX IF NOT EXISTS idx_orders_book_id ON orders(book_id);

-- Таблица запросов
CREATE TABLE IF NOT EXISTS requests (
    id SERIAL PRIMARY KEY,
    book_id INTEGER NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20),
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fulfilled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_requests_book FOREIGN KEY (book_id) 
        REFERENCES books(id) ON DELETE CASCADE
);

-- Индексы для requests
CREATE INDEX IF NOT EXISTS idx_requests_fulfilled ON requests(fulfilled);
CREATE INDEX IF NOT EXISTS idx_requests_book_id ON requests(book_id);
CREATE INDEX IF NOT EXISTS idx_requests_customer ON requests(customer_name);

-- =============================================
-- 2. ФУНКЦИИ И ТРИГГЕРЫ
-- =============================================

-- Функция обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггеры для updated_at
DROP TRIGGER IF EXISTS trigger_books_updated_at ON books;
CREATE TRIGGER trigger_books_updated_at 
    BEFORE UPDATE ON books 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trigger_orders_updated_at ON orders;
CREATE TRIGGER trigger_orders_updated_at 
    BEFORE UPDATE ON orders 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trigger_requests_updated_at ON requests;
CREATE TRIGGER trigger_requests_updated_at 
    BEFORE UPDATE ON requests 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Функция обновления статуса книги
CREATE OR REPLACE FUNCTION update_book_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.quantity <= 0 AND NEW.status = 'IN_STOCK' THEN
        NEW.status := 'OUT_OF_STOCK';
    END IF;
    
    IF NEW.quantity > 0 AND NEW.status = 'OUT_OF_STOCK' THEN
        NEW.status := 'IN_STOCK';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для обновления статуса
DROP TRIGGER IF EXISTS trigger_books_status_update ON books;
CREATE TRIGGER trigger_books_status_update
    BEFORE UPDATE OF quantity ON books
    FOR EACH ROW
    EXECUTE FUNCTION update_book_status();

-- =============================================
-- 3. ЗАПОЛНЕНИЕ ТЕСТОВЫМИ ДАННЫМИ
-- =============================================

-- Очистка таблиц
TRUNCATE TABLE requests CASCADE;
TRUNCATE TABLE orders CASCADE;
TRUNCATE TABLE books CASCADE;

-- Сброс счетчиков
ALTER SEQUENCE books_id_seq RESTART WITH 1;
ALTER SEQUENCE orders_id_seq RESTART WITH 1;
ALTER SEQUENCE requests_id_seq RESTART WITH 1;

-- Вставка книг
INSERT INTO books (
    title, author, isbn, status, quantity, price, 
    publication_date, received_date, description, genre, publisher, pages
) VALUES 
('Война и мир', 'Лев Толстой', '978-5-17-118636-8', 'IN_STOCK', 5, 1500.00, 
 '1869-01-01', '2024-01-15', 'Эпический роман о жизни русского общества в эпоху Наполеоновских войн', 
 'Роман', 'Эксмо', 1300),

('Преступление и наказание', 'Федор Достоевский', '978-5-04-105222-3', 'OUT_OF_STOCK', 0, 1200.00, 
 '1866-01-01', '2024-01-10', 'Психологический роман о студенте Раскольникове, совершившем убийство', 
 'Роман', 'Азбука', 700),

('Мастер и Маргарита', 'Михаил Булгаков', '978-5-17-090381-2', 'IN_STOCK', 3, 1350.00, 
 '1967-01-01', '2024-01-20', 'Философский роман о визите сатаны в Москву 1930-х годов', 
 'Роман', 'АСТ', 480),

('1984', 'Джордж Оруэлл', '978-5-17-102235-8', 'OUT_OF_STOCK', 0, 1100.00, 
 '1949-06-08', '2024-01-25', 'Антиутопия о тоталитарном режиме и Большом Брате', 
 'Фантастика', 'Эксмо', 320),

('Анна Каренина', 'Лев Толстой', '978-5-04-103456-4', 'IN_STOCK', 2, 1400.00, 
 '1877-01-01', '2024-02-01', 'Трагическая история любви замужней женщины и офицера Вронского', 
 'Роман', 'Эксмо', 900),

('Улисс', 'Джеймс Джойс', '978-5-17-101234-2', 'IN_STOCK', 1, 2000.00, 
 '1922-02-02', '2024-02-10', 'Модернистский роман, описывающий один день в Дублине', 
 'Модернизм', 'Иностранка', 750),

('Тихий Дон', 'Михаил Шолохов', '978-5-17-103456-7', 'IN_STOCK', 4, 1600.00, 
 '1928-01-01', '2024-02-15', 'Эпопея о жизни донского казачества в годы Первой мировой и Гражданской войны', 
 'Роман', 'АСТ', 1800),

('Доктор Живаго', 'Борис Пастернак', '978-5-17-104567-8', 'OUT_OF_STOCK', 0, 1300.00, 
 '1957-01-01', '2024-02-20', 'Роман о судьбе интеллигенции в революционной России', 
 'Роман', 'Эксмо', 850);

-- Вставка заказов
INSERT INTO orders (
    book_id, customer_name, customer_phone, customer_email, customer_address, 
    status, order_date, completion_date, total_price, quantity
) VALUES 
(1, 'Иван Петров', '+7-999-123-45-67', 'ivan@mail.ru', 'ул. Ленина, д. 1, кв. 5', 
 'COMPLETED', '2024-01-15 10:30:00', '2024-01-16 14:20:00', 1500.00, 1),

(2, 'Мария Сидорова', '+7-999-234-56-78', 'maria@mail.ru', 'ул. Пушкина, д. 5, кв. 12', 
 'NEW', '2024-01-20 15:00:00', NULL, 1200.00, 1),

(3, 'Алексей Смирнов', '+7-999-345-67-89', 'alex@mail.ru', 'пр. Мира, д. 10, кв. 3', 
 'COMPLETED', '2024-01-25 09:15:00', '2024-01-26 11:30:00', 1350.00, 1),

(5, 'Ольга Новикова', '+7-999-456-78-90', 'olga@mail.ru', 'ул. Гагарина, д. 15, кв. 7', 
 'CANCELLED', '2024-02-01 12:00:00', '2024-02-02 10:00:00', 1400.00, 1),

(1, 'Петр Иванов', '+7-999-567-89-01', 'petr@mail.ru', 'пр. Победы, д. 20, кв. 8', 
 'COMPLETED', '2024-02-05 16:30:00', '2024-02-06 09:00:00', 1500.00, 1),

(6, 'Екатерина Смирнова', '+7-999-678-90-12', 'ekaterina@mail.ru', 'ул. Советская, д. 3, кв. 15', 
 'NEW', '2024-02-10 11:00:00', NULL, 2000.00, 1),

(3, 'Дмитрий Козлов', '+7-999-789-01-23', 'dmitry@mail.ru', 'ул. Лермонтова, д. 7, кв. 2', 
 'COMPLETED', '2024-02-12 14:30:00', '2024-02-13 16:00:00', 1350.00, 1);

-- Вставка запросов
INSERT INTO requests (
    book_id, customer_name, customer_phone, request_date, fulfilled
) VALUES 
(2, 'Мария Сидорова', '+7-999-234-56-78', '2024-01-20 15:00:00', FALSE),

(4, 'Алексей Смирнов', '+7-999-345-67-89', '2024-01-25 09:15:00', FALSE),

(2, 'Дмитрий Козлов', '+7-999-678-90-12', '2024-02-01 14:30:00', FALSE),

(8, 'Иван Петров', '+7-999-123-45-67', '2024-02-20 10:00:00', FALSE),

(4, 'Ольга Новикова', '+7-999-456-78-90', '2024-02-25 12:00:00', FALSE);

-- =============================================
-- 4. ПРОВЕРОЧНЫЕ ЗАПРОСЫ
-- =============================================

-- Количество книг
SELECT 'books' as table_name, COUNT(*) as count FROM books
UNION ALL
SELECT 'orders', COUNT(*) FROM orders
UNION ALL
SELECT 'requests', COUNT(*) FROM requests;

-- Статистика по статусам книг
SELECT status, COUNT(*) as count FROM books GROUP BY status;

-- Статистика по статусам заказов
SELECT status, COUNT(*) as count FROM orders GROUP BY status;

-- Проверка внешних ключей
SELECT 
    b.id as book_id,
    b.title as book_title,
    COUNT(o.id) as orders_count,
    COUNT(r.id) as requests_count
FROM books b
LEFT JOIN orders o ON b.id = o.book_id
LEFT JOIN requests r ON b.id = r.book_id
GROUP BY b.id, b.title
ORDER BY b.id;