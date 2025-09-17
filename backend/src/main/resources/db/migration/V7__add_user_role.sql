-- Migration: Add USER role to role enum
-- This migration adds the USER role to the existing role enum

-- Step 1: Update the role column to support USER role
ALTER TABLE users MODIFY COLUMN role ENUM('ADMIN','MANAGER','SALE','USER') NOT NULL DEFAULT 'USER';

-- Step 2: Update existing records to have a default role if needed
-- This ensures all existing users have a valid role
UPDATE users SET role = 'ADMIN' WHERE role IS NULL OR role = '';

-- Note: This migration preserves all existing data and adds support for USER role
-- The default role for new users will be USER
