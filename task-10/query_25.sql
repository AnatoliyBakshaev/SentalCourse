-- Найти производителей принтеров, которые производят ПК с наименьшим объемом RAM и с самым быстрым процессором среди всех ПК, имеющих наименьший объем RAM. Вывести поля: maker
SELECT DISTINCT p.maker
FROM Product p
JOIN PC pc ON p.model = pc.model
WHERE pc.ram = (SELECT MIN(ram) FROM PC)
  AND pc.speed = (SELECT MAX(speed) FROM PC WHERE ram = (SELECT MIN(ram) FROM PC))
  AND p.maker IN (SELECT DISTINCT maker FROM Product WHERE type = 'Printer');