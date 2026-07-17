-- Найти принтеры, имеющие самую высокую цену. Вывести поля: model, price.
SELECT model, price::numeric
FROM Printer
WHERE price = (SELECT MAX(price) FROM Printer);