  # --- !Ups

  INSERT INTO "sport_disciplines"("name", "name_key") VALUES ('Basketball', 'discipline.basketball');
  INSERT INTO "sport_disciplines"("name", "name_key") VALUES ('Football', 'discipline.football');
  INSERT INTO "sport_disciplines"("name", "name_key") VALUES ('Volleyball', 'discipline.volleyball');

  # --- !Downs

  DELETE FROM "sport_disciplines" WHERE name = 'Basketball';
  DELETE FROM "sport_disciplines" WHERE name = 'Football';
  DELETE FROM "sport_disciplines" WHERE name = 'Volleyball';