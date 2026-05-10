MERGE INTO categories (id, name) KEY (id)
VALUES (1, 'Електроніка');
MERGE INTO categories (id, name) KEY (id)
VALUES (2, 'Аксесуари');

MERGE INTO products (id, name, price, stock_quantity, category_id, owner_id) KEY (id)
VALUES (1, 'Ноутбук', 32000.0, 6, 1, 1);
MERGE INTO products (id, name, price, stock_quantity, category_id, owner_id) KEY (id)
VALUES (2, 'Мишка', 900.0, 20, 1, 1);
MERGE INTO products (id, name, price, stock_quantity, category_id, owner_id) KEY (id)
VALUES (3, 'Чохол', 500.0, 50, 2, 1);
