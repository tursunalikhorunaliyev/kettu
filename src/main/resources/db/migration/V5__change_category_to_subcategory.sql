ALTER TABLE category_tags RENAME TO subcategory_tags;
ALTER TABLE subcategory_tags RENAME COLUMN category_id TO subcategory_id;
ALTER TABLE subcategory_tags RENAME CONSTRAINT uk_category_tag TO uk_subcategory_tag;

ALTER TABLE category RENAME TO sub_category;