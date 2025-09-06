-- Migration V3: Add slug columns for products and customers
-- Adding slug columns with unique constraints for SEO-friendly URLs

-- Add slug column to products table (nullable first)
ALTER TABLE products ADD COLUMN slug VARCHAR(150) AFTER name;

-- Add slug column to customers table (nullable first)
ALTER TABLE customers ADD COLUMN slug VARCHAR(180) AFTER name;

-- Create indexes for better performance on slug lookups
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_customers_slug ON customers(slug);

-- Populate slug values for existing records
-- For products: simple slug generation
UPDATE products 
SET slug = LOWER(REPLACE(name, ' ', '-'))
WHERE slug IS NULL;

-- For customers: simple slug generation  
UPDATE customers 
SET slug = LOWER(REPLACE(name, ' ', '-'))
WHERE slug IS NULL;

-- Handle duplicates by appending ID
UPDATE products 
SET slug = CONCAT(slug, '-', id)
WHERE id > 1;

UPDATE customers 
SET slug = CONCAT(slug, '-', id)
WHERE id > 1;

-- Now add NOT NULL UNIQUE constraints after all slugs are populated
ALTER TABLE products MODIFY COLUMN slug VARCHAR(150) NOT NULL UNIQUE;
ALTER TABLE customers MODIFY COLUMN slug VARCHAR(180) NOT NULL UNIQUE;