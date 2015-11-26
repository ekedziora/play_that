  # --- !Ups

create table "user"
(
  "userID" UUID NOT NULL PRIMARY KEY,
  "username" TEXT,
  "firstName" TEXT,
  "lastName" TEXT,
  "email" TEXT,
  "gender" VARCHAR(1),
  "birthDate" DATE,
  "avatarURL" TEXT,
  constraint username_unique_index UNIQUE(username),
  constraint email_unique_index UNIQUE(email)
);

create table "logininfo"
(
  "id" SERIAL NOT NULL PRIMARY KEY,
  "providerID" TEXT NOT NULL,
  "providerKey" TEXT NOT NULL
);

create table "userlogininfo"
(
  "userID" UUID NOT NULL,
  "loginInfoId" BIGINT NOT NULL,
  constraint fk_user_login_info_login_info foreign key ("loginInfoId") references logininfo(id),
  constraint fk_user_login_info_user_id foreign key ("userID") references "user"("userID")
);

create table "passwordinfo"
(
  "hasher" TEXT NOT NULL,
  "password" TEXT NOT NULL,
  "salt" TEXT,
  "loginInfoId" BIGINT NOT NULL,
  constraint fk_password_info_login_info foreign key ("loginInfoId") REFERENCES logininfo(id)
);

create table "oauth2info"
(
  "id" SERIAL NOT NULL PRIMARY KEY,
  "accesstoken" TEXT NOT NULL,
  "tokentype" TEXT,
  "expiresin" INTEGER,
  "refreshtoken" TEXT,
  "loginInfoId" BIGINT NOT NULL,
  constraint fk_oauth2_info_login_info foreign key ("loginInfoId") REFERENCES logininfo(id)
);


# --- !Downs

drop table if exists "openidattributes";
drop table if exists "openidinfo";
drop table if exists "oauth2info";
drop table if exists "oauth1info";
drop table if exists "passwordinfo";
drop table if exists "userlogininfo";
drop table if exists "logininfo";
drop table if exists "user";