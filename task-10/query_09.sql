-- Найти производителей ПК с процессором не менее 450 МГц. Вывести поля: maker.
SELECT DISTINCT p.maker
FROM Product p
JOIN PC pc ON p.model = pc.model
WHERE pc.speed >= 450;