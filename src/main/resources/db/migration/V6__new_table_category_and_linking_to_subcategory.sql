CREATE TABLE category(
      id SERIAL PRIMARY KEY,
      name VARCHAR(100) NOT NULL UNIQUE
);

ALTER TABLE sub_category ADD COLUMN category_id INTEGER;
ALTER TABLE sub_category ADD CONSTRAINT fk_sub_category_parent_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE RESTRICT