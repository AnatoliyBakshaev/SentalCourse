-- Для каждого производителя найти средний размер экрана выпускаемых им ноутбуков. Вывести поля: maker, средний размер экрана.
SELECT p.maker, AVG(l.screen) AS avg_screen_size
FROM Laptop l
JOIN Product p ON l.model = p.model
GROUP BY p.maker
ORDER BY p.maker;