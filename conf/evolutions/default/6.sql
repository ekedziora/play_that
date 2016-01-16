  # --- !Ups

alter table "event_participants" add column "present" boolean;

alter table "events" add column "presence_reported" boolean not null default false;

  # --- !Downs

alter table "event_participants" drop column "present";

alter table "events" drop column "presence_reported";