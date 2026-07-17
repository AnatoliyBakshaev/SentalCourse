-- Найти среднюю скорость ноутбуков, цена которых превышает 1000 долларов.
SELECT AVG(speed) AS avg_speed
FROM Laptop
WHERE price > 1000::money;