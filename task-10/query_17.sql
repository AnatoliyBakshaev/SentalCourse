-- Найти модели ноутбуков, скорость которых меньше скорости любого из ПК. Вывести поля: type, model, speed.
SELECT p.type, l.model, l.speed
FROM Laptop l
JOIN Product p ON l.model = p.model
WHERE l.speed < ALL (SELECT speed FROM PC);