  # --- !Ups

create table "user_mail_tokens"
(
  "id" UUID NOT NULL PRIMARY KEY,
  "userId" UUID NOT NULL,
  "expiration_time" TIMESTAMP NOT NULL,
  "is_sign_up" BOOLEAN NOT NULL,
  constraint fk_user_mail_token_user_id foreign key ("userId") REFERENCES "user"("userID")
);

alter table "user" add column "emailConfirmed" boolean not null default false;

  # --- !Downs

drop table if exists "user_mail_tokens";

alter table "user" drop column "emailConfirmed";