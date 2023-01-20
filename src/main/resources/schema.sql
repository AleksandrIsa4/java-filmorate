DROP TABLE IF EXISTS
    "genre",
    "user_kino",
    "mpa",
    directors,
    "film",
    films_to_directors,
    "friends",
    "genre_film",
    "like_users",
    "reviews",
    "like_review",
    "dislike_review"
    CASCADE;

CREATE TABLE IF NOT EXISTS PUBLIC."genre" (
	id INTEGER PRIMARY KEY,
	"genre" CHARACTER VARYING(255) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_4 ON PUBLIC."genre" (id);
MERGE INTO PUBLIC."genre" KEY (id) VALUES (1, 'Комедия');
MERGE INTO PUBLIC."genre" KEY (id) VALUES (2, 'Драма');
MERGE INTO PUBLIC."genre" KEY (id) VALUES (3, 'Мультфильм');
MERGE INTO PUBLIC."genre" KEY (id) VALUES (4, 'Триллер');
MERGE INTO PUBLIC."genre" KEY (id) VALUES (5, 'Документальный');
MERGE INTO PUBLIC."genre" KEY (id) VALUES (6, 'Боевик');

CREATE TABLE IF NOT EXISTS PUBLIC."user_kino" (
	"user_id" INTEGER PRIMARY KEY,
	"email" CHARACTER VARYING(255) NOT NULL,
	"login" CHARACTER VARYING(255) NOT NULL,
	"name" CHARACTER VARYING(255),
	"birthday" DATE
);
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_28 ON PUBLIC."user_kino" ("user_id");

CREATE TABLE IF NOT EXISTS PUBLIC."mpa" (
	id INTEGER PRIMARY KEY,
	"rating" CHARACTER VARYING(255) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_F ON PUBLIC."mpa" (id);
MERGE INTO PUBLIC."mpa" KEY (id) VALUES (1, 'G');
MERGE INTO PUBLIC."mpa" KEY (id) VALUES (2, 'PG');
MERGE INTO PUBLIC."mpa" KEY (id) VALUES (3, 'PG-13');
MERGE INTO PUBLIC."mpa" KEY (id) VALUES (4, 'R');
MERGE INTO PUBLIC."mpa" KEY (id) VALUES (5, 'NC-17');

CREATE TABLE IF NOT EXISTS directors (
    director_id int NOT NULL auto_increment,
    name text NOT NULL,
    CONSTRAINT pk_director PRIMARY KEY (director_id)
);

CREATE TABLE IF NOT EXISTS PUBLIC."film" (
	"film_id" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"name" CHARACTER VARYING(255) NOT NULL,
	"description" CHARACTER VARYING(255),
	"release_date" DATE,
	"duration" INTEGER,
	"rate" INTEGER,
	"rating_id" INTEGER,
	CONSTRAINT "fk_FIlm_RatingID" FOREIGN KEY ("rating_id") REFERENCES PUBLIC."mpa"(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_2 ON PUBLIC."film" ("film_id");
CREATE INDEX IF NOT EXISTS "fk_FIlm_RatingID_INDEX_2" ON PUBLIC."film" ("rating_id");

CREATE TABLE IF NOT EXISTS films_to_directors (
    film_id int NOT NULL,
    director_id int NOT NULL,
    CONSTRAINT "fk_films_to_directors_film_id" FOREIGN KEY (film_id)
        REFERENCES "film" ("film_id") ON DELETE CASCADE,
    CONSTRAINT "fk_films_to_directors_director_id" FOREIGN KEY (director_id)
        REFERENCES directors (director_id) ON DELETE CASCADE,
    CONSTRAINT "uc_films_to_directors" UNIQUE (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS PUBLIC."friends" (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"user_id" CHARACTER VARYING(255) NOT NULL,
	"friend_id" CHARACTER VARYING(255) NOT NULL,
	"friendship" boolean,
	CONSTRAINT "fk_Friends_FriendId" FOREIGN KEY ("friend_id") REFERENCES PUBLIC."user_kino"("user_id") ON DELETE CASCADE ON UPDATE RESTRICT,
	CONSTRAINT "fk_Friends_UserId" FOREIGN KEY ("user_id") REFERENCES PUBLIC."user_kino"("user_id") ON DELETE CASCADE ON UPDATE RESTRICT
);
--CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_3 ON PUBLIC."friends" (id);
CREATE INDEX IF NOT EXISTS "fk_Friends_FriendId_INDEX_3" ON PUBLIC."friends" ("friend_id");
CREATE INDEX IF NOT EXISTS "fk_Friends_Friendship_INDEX_3" ON PUBLIC."friends" ("friendship");
CREATE INDEX IF NOT EXISTS "fk_Friends_UserId_INDEX_3" ON PUBLIC."friends" ("user_id");

CREATE TABLE IF NOT EXISTS PUBLIC."genre_film" (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"film_id" INTEGER NOT NULL,
    "genre_id" INTEGER NOT NULL,
	CONSTRAINT "fk_GenreFilm_FilmId" FOREIGN KEY ("film_id") REFERENCES PUBLIC."film"("film_id") ON DELETE CASCADE ON UPDATE RESTRICT,
	CONSTRAINT "fk_GenreFilm_GenreID" FOREIGN KEY ("genre_id") REFERENCES PUBLIC."genre"(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
--CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_9 ON PUBLIC."genre_film" (id);
CREATE INDEX IF NOT EXISTS "fk_GenreFilm_FilmId_INDEX_9" ON PUBLIC."genre_film" ("film_id");
CREATE INDEX IF NOT EXISTS "fk_GenreFilm_GenreID_INDEX_9" ON PUBLIC."genre_film" ("genre_id");

CREATE TABLE IF NOT EXISTS PUBLIC."like_users" (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"user_id" INTEGER NOT NULL,
	"film_id" INTEGER NOT NULL,
	CONSTRAINT "fk_LikeUsers_FilmId" FOREIGN KEY ("film_id") REFERENCES PUBLIC."film"("film_id") ON DELETE CASCADE ON UPDATE RESTRICT,
	CONSTRAINT "fk_LikeUsers_UserId" FOREIGN KEY ("user_id") REFERENCES PUBLIC."user_kino"("user_id") ON DELETE CASCADE ON UPDATE RESTRICT
);
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_6 ON PUBLIC."like_users" (id);
CREATE INDEX IF NOT EXISTS "fk_LikeUsers_FilmId_INDEX_6" ON PUBLIC."like_users" ("film_id");
CREATE INDEX IF NOT EXISTS "fk_LikeUsers_UserId_INDEX_6" ON PUBLIC."like_users" ("user_id");


CREATE TABLE IF NOT EXISTS PUBLIC."reviews"
(
    id            INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "content"     CHARACTER VARYING(255) NOT NULL,
    "is_positive" boolean,
    "user_id"     INTEGER                NOT NULL,
    "film_id"     INTEGER                NOT NULL,
    "useful"      INTEGER,
    CONSTRAINT "fk_reviews_Users_FilmId" FOREIGN KEY ("film_id") REFERENCES PUBLIC."film" ("film_id") ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT "fk_reviews_Users_UserId" FOREIGN KEY ("user_id") REFERENCES PUBLIC."user_kino" ("user_id") ON DELETE RESTRICT ON UPDATE RESTRICT
    );
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_7 ON PUBLIC."reviews" (id);
CREATE INDEX IF NOT EXISTS "fk_reviews_Users_FilmId_INDEX_7" ON PUBLIC."reviews" ("film_id");
CREATE INDEX IF NOT EXISTS "fk_reviews_Users_UserId_INDEX_7" ON PUBLIC."reviews" ("user_id");

CREATE TABLE IF NOT EXISTS PUBLIC."like_review"
(
    review_id INTEGER REFERENCES "reviews" (id) ON DELETE CASCADE ,
    user_id INTEGER REFERENCES "user_kino" ("user_id") ON DELETE RESTRICT,
    CONSTRAINT pk_like_review PRIMARY KEY (review_id, user_id)

    );

CREATE TABLE IF NOT EXISTS PUBLIC."dislike_review"
(
    review_id INTEGER REFERENCES "reviews" (id) ON DELETE CASCADE ,
    user_id INTEGER REFERENCES "user_kino" ("user_id") ON DELETE RESTRICT,
    CONSTRAINT pk_dislike_review PRIMARY KEY (review_id, user_id)
    );