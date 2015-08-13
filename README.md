# communicator
A Scala/Akka/Phantom service designed to provide a communication platform for person to person chat.

Schema:

CREATE TABLE users (
      username text PRIMARY KEY,
      first_name text,
      last_name text,
      email text,
    ) WITH COMPACT STORAGE;

CREATE TABLE messages (
    id uuid,
    from_user text,
    to_user text,
    submitted timestamp,
    text text,
    PRIMARY KEY ((from_user, to_user), submitted)
) WITH CLUSTERING ORDER BY (submitted DESC);

INSERT INTO users (username, first_name, last_name, email)
  VALUES('justin', 'Justin', 'Miller', 'justinrmiller@gmail.com');