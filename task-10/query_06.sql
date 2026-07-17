-- Указать производителя и скорость для тех ноутбуков, которые имеют жесткий диск объемом не менее 100 Гбайт.
SELECT p.maker, l.speed
FROM Laptop l
JOIN Product p ON l.model = p.model
WHERE l.hd >= 100;