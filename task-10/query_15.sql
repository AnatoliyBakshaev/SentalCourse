-- Найти размеры жестких дисков, совпадающих у двух и более PC. Вывести поля: hd.
SELECT hd
FROM PC
GROUP BY hd
HAVING COUNT(*) >= 2;