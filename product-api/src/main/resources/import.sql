INSERT INTO category(id, description) VALUES(1000, 'Comic Books');
INSERT INTO category(id, description) VALUES(1001, 'Movies');
INSERT INTO category(id, description) VALUES(1002, 'Books');

INSERT INTO supplier(id, name) VALUES(1000, 'Panini Comics');
INSERT INTO supplier(id, name) VALUES(1001, 'Amazon');

INSERT INTO product(id, name, quantity_available, created_at, fk_supplier, fk_category) VALUES(1000, 'Crise nas Infinitas Terras', 10, CURRENT_TIMESTAMP, 1000, 1000);
INSERT INTO product(id, name, quantity_available, created_at, fk_supplier, fk_category) VALUES(1001, 'Interestelar', 5, CURRENT_TIMESTAMP, 1001, 1001);
INSERT INTO product(id, name, quantity_available, created_at, fk_supplier, fk_category) VALUES(1002, 'Harry Potter e a Pedra Filosofal', 3, CURRENT_TIMESTAMP, 1001, 1002);
