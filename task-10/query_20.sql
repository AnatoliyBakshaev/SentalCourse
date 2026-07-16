-- Найти производителей, выпускающих по меньшей мере три различных модели ПК. Вывести поля: maker, число моделей.
SELECT p.maker, COUNT(DISTINCT pc.model) AS model_count
FROM Product p
JOIN PC pc ON p.model = pc.model
GROUP BY p.maker
HAVING COUNT(DISTINCT pc.model) >= 3;