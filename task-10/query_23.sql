-- Найти производителей, которые производили бы как ПК, так и ноутбуки со скоростью не менее 750 МГц. Вывести поля: maker
SELECT DISTINCT p1.maker
FROM Product p1
JOIN PC pc ON p1.model = pc.model
WHERE pc.speed >= 750
  AND p1.maker IN (
      SELECT DISTINCT p2.maker
      FROM Product p2
      JOIN Laptop l ON p2.model = l.model
      WHERE l.speed >= 750
  );