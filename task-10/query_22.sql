-- Для каждого значения скорости процессора ПК, превышающего 600 МГц, найти среднюю цену ПК с такой же скоростью. Вывести поля: speed, средняя цена.
SELECT speed, AVG(price::numeric) AS avg_price
FROM PC
WHERE speed > 600
GROUP BY speed
ORDER BY speed;