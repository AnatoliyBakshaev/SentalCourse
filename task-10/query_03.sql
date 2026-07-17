-- Найти номер модели, объем памяти и размеры экранов ноутбуков, цена которых превышает 1000 долларов.
SELECT model, ram, screen
FROM Laptop
WHERE price > 1000::money;