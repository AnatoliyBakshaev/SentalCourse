-- =============================================
-- DDL: Создание таблиц для книжного магазина
-- База данных: PostgreSQL
-- =============================================

-- Создание базы данных (если не существует)
-- CREATE DATABASE bookstore;

-- Подключение к базе данных
-- \c bookstore;

-- =============================================
-- Таблица книг
-- =============================================
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

-- Индексы для таблицы books
CREATE INDEX idx_books_status ON books(status);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);

-- =============================================
-- Таблица заказов
-- =============================================
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

-- Индексы для таблицы orders
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_orders_customer ON orders(customer_name);
CREATE INDEX idx_orders_book_id ON orders(book_id);

-- =============================================
-- Таблица запросов
-- =============================================
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

-- Индексы для таблицы requests
CREATE INDEX idx_requests_fulfilled ON requests(fulfilled);
CREATE INDEX idx_requests_book_id ON requests(book_id);
CREATE INDEX idx_requests_customer ON requests(customer_name);

-- =============================================
-- Функция обновления updated_at
-- =============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггеры для обновления updated_at
CREATE TRIGGER trigger_books_updated_at 
    BEFORE UPDATE ON books 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_orders_updated_at 
    BEFORE UPDATE ON orders 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_requests_updated_at 
    BEFORE UPDATE ON requests 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- =============================================
-- Функция обновления статуса книги при изменении количества
-- =============================================
CREATE OR REPLACE FUNCTION update_book_status()
RETURNS TRIGGER AS $$
BEGIN
    -- Если количество <= 0 и статус "IN_STOCK" -> меняем на "OUT_OF_STOCK"
    IF NEW.quantity <= 0 AND NEW.status = 'IN_STOCK' THEN
        NEW.status := 'OUT_OF_STOCK';
    END IF;
    
    -- Если количество > 0 и статус "OUT_OF_STOCK" -> меняем на "IN_STOCK"
    IF NEW.quantity > 0 AND NEW.status = 'OUT_OF_STOCK' THEN
        NEW.status := 'IN_STOCK';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для обновления статуса книги
CREATE TRIGGER trigger_books_status_update
    BEFORE UPDATE OF quantity ON books
    FOR EACH ROW
    EXECUTE FUNCTION update_book_status();