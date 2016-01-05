  # --- !Ups

  CREATE TABLE "sport_disciplines" (
    "id" SERIAL NOT NULL PRIMARY KEY,
    "name" TEXT NOT NULL,
    "name_key" TEXT NOT NULL,
    CONSTRAINT "unique_name" UNIQUE (name),
    CONSTRAINT "unique_name_key" UNIQUE (name_key)
  );

  CREATE TABLE "events" (
    "id" SERIAL NOT NULL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "description" TEXT,
    "date_time" TIMESTAMP NOT NULL,
    "max_participants" INT,
    "owner_id" UUID NOT NULL,
    "discipline_id" BIGINT NOT NULL,
    CONSTRAINT "fk_event_owner" FOREIGN KEY ("owner_id") REFERENCES "user"("userID"),
    CONSTRAINT "fk_event_discipline" FOREIGN KEY ("discipline_id") REFERENCES "sport_disciplines"("id"),
    CONSTRAINT "max_participants_greather_than_zero" CHECK (max_participants > 0)
  );

  CREATE TABLE "event_participants" (
    "id" SERIAL NOT NULL PRIMARY KEY,
    "event_id" BIGINT NOT NULL,
    "user_id" UUID NOT NULL,
    CONSTRAINT "fk_event_participants_event" FOREIGN KEY ("event_id") REFERENCES "events"("id"),
    CONSTRAINT "fk_event_participants_user" FOREIGN KEY ("user_id") REFERENCES "user"("userID")
  );

  # --- !Downs

  drop table if exists "event_participants";
  drop table if exists "events";
  drop table if exists "sport_disciplines";