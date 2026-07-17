-- Найти номера моделей и цены всех продуктов (любого типа), выпущенных производителем B.
SELECT p.model, 
       COALESCE(pc.price::numeric, l.price::numeric, pr.price::numeric) AS price
FROM Product p
LEFT JOIN PC pc ON p.model = pc.model
LEFT JOIN Laptop l ON p.model = l.model
LEFT JOIN Printer pr ON p.model = pr.model
WHERE p.maker = 'B';