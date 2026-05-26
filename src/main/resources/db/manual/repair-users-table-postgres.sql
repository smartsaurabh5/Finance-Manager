-- Manual PostgreSQL repair for the users table.
-- Run this only after taking a database backup.
-- The application maps by column name; this script aligns the physical schema
-- and repairs rows that were left null by schema drift.

begin;

-- 1. Preserve the current table exactly as it is before touching data.
create table if not exists users_backup_before_mapping_repair as
select *
from users;

-- 2. Ensure the columns expected by com.finance.manager.entity.User exist.
alter table users
    add column if not exists failed_login_attempts integer,
    add column if not exists locked_until timestamp,
    add column if not exists last_login_at timestamp,
    add column if not exists last_activity_at timestamp,
    add column if not exists created_at timestamp,
    add column if not exists role varchar(30);

-- 3. Normalize values that Hibernate cannot safely infer for old rows.
update users
set failed_login_attempts = 0
where failed_login_attempts is null
   or failed_login_attempts < 0;

update users
set role = 'USER'
where role is null
   or role = '';

update users
set created_at = current_timestamp
where created_at is null;

-- 4. Apply defaults and constraints after old rows have been backfilled.
alter table users
    alter column failed_login_attempts set default 0,
    alter column failed_login_attempts set not null,
    alter column role set default 'USER',
    alter column role set not null,
    alter column created_at set not null;

-- 5. Verify the rows that matter for account lockout.
select id,
       username,
       failed_login_attempts,
       last_login_at,
       locked_until,
       last_activity_at,
       role,
       created_at
from users
order by id;

commit;
