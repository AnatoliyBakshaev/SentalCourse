-- Найти производителя, выпускающего ПК, но не ноутбуки.
SELECT DISTINCT maker
FROM Product
WHERE type = 'PC'
EXCEPT
SELECT DISTINCT maker
FROM Product
WHERE type = 'Laptop';