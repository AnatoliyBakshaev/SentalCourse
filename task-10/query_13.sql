-- Найти среднюю скорость ПК, выпущенных производителем A.
SELECT AVG(pc.speed) AS avg_speed
FROM PC pc
JOIN Product p ON pc.model = p.model
WHERE p.maker = 'A';