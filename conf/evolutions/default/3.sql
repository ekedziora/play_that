  # --- !Ups

  INSERT INTO "sport_disciplines"("name", "name_key", "photo_name") VALUES ('Basketball', 'discipline.basketball', 'Basketball-Tryouts.jpg');
  INSERT INTO "sport_disciplines"("name", "name_key", "photo_name") VALUES ('Football', 'discipline.football', 'football-fiesta-salisbury.jpg');
  INSERT INTO "sport_disciplines"("name", "name_key", "photo_name") VALUES ('Volleyball', 'discipline.volleyball', 'volleyball_pix_medium.jpg');

  # --- !Downs

  DELETE FROM "sport_disciplines" WHERE name = 'Basketball';
  DELETE FROM "sport_disciplines" WHERE name = 'Football';
  DELETE FROM "sport_disciplines" WHERE name = 'Volleyball';