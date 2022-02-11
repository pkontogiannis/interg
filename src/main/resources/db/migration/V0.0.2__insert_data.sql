
INSERT INTO stock_keeping.store(name) values ('Store 1');
INSERT INTO stock_keeping.store(name) values ('Store 2');
-- INSERT INTO stock_keeping.store(name) values ('Store 3');
-- INSERT INTO stock_keeping.store(name) values ('Store 4');
--
-- INSERT INTO stock_keeping.article(name) values ('Article 1');
-- INSERT INTO stock_keeping.article(name) values ('Article 2');
-- INSERT INTO stock_keeping.article(name) values ('Article 3');
-- INSERT INTO stock_keeping.article(name) values ('Article 4');
--
-- INSERT INTO stock_keeping.store_article(article_id, store_id, available_stock) values (1, 1, 10);
-- INSERT INTO stock_keeping.store_article(article_id, store_id, available_stock) values (2, 1, 4);
-- INSERT INTO stock_keeping.store_article(article_id, store_id, available_stock) values (1, 2, 5);
-- INSERT INTO stock_keeping.store_article(article_id, store_id, available_stock) values (3, 2, 67);

CREATE OR REPLACE FUNCTION remove_expired_reservations() RETURNS void AS
$$
WITH expired AS (
    DELETE FROM stock_keeping.reserved_article
        WHERE  reservation_time < NOW() - INTERVAL '5 minute'
        RETURNING *
), expiredsum AS (
    SELECT store_id, article_id,  SUM(reserved_stock) as reserved_stock
    FROM expired
    GROUP BY store_id, article_id
)
UPDATE stock_keeping.store_article AS sa
SET available_stock = sa.available_stock + e.reserved_stock
FROM expiredsum AS e
WHERE (e.store_id = sa.store_id and e.article_id = sa.article_id)
$$ LANGUAGE sql;

