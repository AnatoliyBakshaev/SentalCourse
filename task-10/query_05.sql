-- Найти номер модели, скорость и размер жесткого диска для ПК, имеющих скорость cd 12x или 24x и цену менее 600 долларов.
SELECT model, speed, hd
FROM PC
WHERE cd IN ('12x', '24x')
  AND price < 600::money;