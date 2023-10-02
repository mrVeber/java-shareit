CREATE TABLE IF NOT EXISTS USERS
(
    ID    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    NAME  VARCHAR(255)                            NOT NULL,
    EMAIL VARCHAR(512) UNIQUE                     NOT NULL
);

CREATE TABLE IF NOT EXISTS ITEMS
(
    ID          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME        VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(512) NOT NULL,
    AVAILABLE   BOOL         NOT NULL,
    OWNER_ID    BIGINT REFERENCES USERS (ID) ON DELETE CASCADE,
    REQUEST_ID  BIGINT
);

CREATE TABLE IF NOT EXISTS BOOKINGS
(
    ID         BIGINT GENERATED ALWAYS AS IDENTITY            NOT NULL PRIMARY KEY,
    START_DATE TIMESTAMP WITHOUT TIME ZONE                    NOT NULL,
    END_DATE   TIMESTAMP WITHOUT TIME ZONE                    NOT NULL,
    ITEM_ID    BIGINT REFERENCES ITEMS (ID) ON DELETE CASCADE NOT NULL,
    BOOKER_ID  BIGINT REFERENCES USERS (ID) ON DELETE CASCADE NOT NULL,
    STATUS     VARCHAR(16)                                    NOT NULL
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    ID           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    DESCRIPTION  VARCHAR(256)                NOT NULL,
    REQUESTER_ID BIGINT REFERENCES USERS (ID) ON DELETE CASCADE,
    CREATED      TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    ID        BIGINT GENERATED ALWAYS AS IDENTITY            NOT NULL PRIMARY KEY,
    TEXT      VARCHAR(512)                                   NOT NULL,
    ITEM_ID   BIGINT REFERENCES ITEMS (ID) ON DELETE CASCADE NOT NULL,
    AUTHOR_ID BIGINT REFERENCES USERS (ID) ON DELETE CASCADE NOT NULL,
    CREATED   TIMESTAMP WITHOUT TIME ZONE                    NOT NULL
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    ID           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    REQUESTER_ID BIGINT REFERENCES USERS (ID) NOT NULL,
    DESCRIPTION  VARCHAR(256)                 NOT NULL,
    CREATED      TIMESTAMP WITHOUT TIME ZONE  NOT NULL
);