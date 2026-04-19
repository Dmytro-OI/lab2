INSERT INTO users (username, email) VALUES ('admin', 'dmytro@lpnu.ua');

INSERT INTO categories (name) VALUES ('Електроніка');
INSERT INTO categories (name) VALUES ('Аксесуари');

INSERT INTO products (name, price, stock_quantity, category_id, owner_id) VALUES ('Ноутбук', 32000.0, 6, 1, 1);
INSERT INTO products (name, price, stock_quantity, category_id, owner_id) VALUES ('Мишка', 900.0, 20, 1, 1);
INSERT INTO products (name, price, stock_quantity, category_id, owner_id) VALUES ('Чохол', 500.0, 50, 2, 1);
