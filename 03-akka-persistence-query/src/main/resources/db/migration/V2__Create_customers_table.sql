DROP TABLE IF EXISTS PUBLIC."customers";

CREATE TABLE IF NOT EXISTS PUBLIC."customer_accounts" (
  "username" VARCHAR(255) PRIMARY KEY,
  "point_balance" INT NOT NULL,
  "created_at" TIMESTAMP NOT NULL,
  "updated_at" TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC."vouchers" (
  "code" VARCHAR(255) NOT NULL PRIMARY KEY,
  "username" VARCHAR(255) NOT NULL,
  "value" DOUBLE NOT NULL,
  FOREIGN KEY ("username") REFERENCES PUBLIC."customer_accounts"("username")
);

CREATE INDEX IDX1 ON PUBLIC."vouchers"("username")
