# --- !Ups

alter table "user" drop column "fullName";
alter table "user" add column "username" VARCHAR;
alter table "user" add constraint "username_unique_index" unique ("username");
alter table "user" add constraint "email_unique_index" unique ("email");

# --- !Downs

alter table "user" add column fullName VARCHAR;
alter table "user" drop column "username";
alter table "user" drop constraint "username_unique_index";
alter table "user" drop constraint "email_unique_index";