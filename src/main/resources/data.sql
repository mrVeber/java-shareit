delete
from ITEMS;
delete
from USERS;
delete
from bookings;
delete
from comments;

ALTER TABLE USERS
    ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE ITEMS
    ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE bookings
    ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE comments
    ALTER COLUMN ID RESTART WITH 1;