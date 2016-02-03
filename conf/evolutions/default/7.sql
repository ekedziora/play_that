  # --- !Ups
alter table "events" add column "lat" double precision not null default 0;
alter table "events" add column "lng" double precision not null default 0;

  # --- !Downs
alter table "events" drop column "lat";
alter table "events" drop column "lng";