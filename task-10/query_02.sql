-- Найти производителей принтеров. Вывести поля: maker.
SELECT DISTINCT maker
FROM Product
WHERE type = 'Printer';