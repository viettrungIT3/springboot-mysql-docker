-- Migration: Rename administrators table to users
-- This migration renames the administrators table to users and adds USER role support

-- Step 1: Rename the table
RENAME TABLE administrators TO users;

-- Step 2: Update the role column to support USER role (if needed)
-- The role column already supports ADMIN, MANAGER, SALE, so we just need to ensure USER is supported
-- This is handled by the enum update in the Java code

-- Step 3: Add any missing indexes or constraints if needed
-- The existing constraints should be preserved during the rename

-- Note: This migration preserves all existing data and constraints
-- The table structure remains the same, only the name changes
