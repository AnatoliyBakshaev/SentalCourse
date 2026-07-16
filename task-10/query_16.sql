-- Найти пары моделей PC, имеющих одинаковые скорость процессора и RAM. В результате каждая пара указывается только один раз, т.е. (i,j), но не (j,i). Порядок вывода полей: модель с большим номером, модель с меньшим номером, скорость, RAM.
SELECT 
    CASE WHEN pc1.model > pc2.model THEN pc1.model ELSE pc2.model END AS model1,
    CASE WHEN pc1.model > pc2.model THEN pc2.model ELSE pc1.model END AS model2,
    pc1.speed,
    pc1.ram
FROM PC pc1
JOIN PC pc2 ON pc1.speed = pc2.speed 
            AND pc1.ram = pc2.ram
            AND pc1.model <> pc2.model
            AND pc1.model > pc2.model;