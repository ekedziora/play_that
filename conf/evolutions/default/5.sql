  # --- !Ups

alter table "user" alter column "username" set not null;
alter table "user" alter column "email" set not null;

  # --- !Downs

alter table "user" alter column "username" drop not null;
alter table "user" alter column "email" drop not null;