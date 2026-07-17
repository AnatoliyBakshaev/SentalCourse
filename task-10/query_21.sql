-- Найти максимальную цену ПК, выпускаемых каждым производителем. Вывести поля: maker, максимальная цена.
SELECT p.maker, MAX(pc.price::numeric) AS max_price
FROM Product p
JOIN PC pc ON p.model = pc.model
GROUP BY p.maker
ORDER BY p.maker;