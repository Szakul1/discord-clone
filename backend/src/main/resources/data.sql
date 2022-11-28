INSERT Into DISCORD_USER (username, password, email, enabled) values ('test', '$2a$10$9EzWIeyDk4E3u1O1SpyPS.9XyK/W6jw3QJ/cp9urAZ81PB5lvnJY6', 'test', true);

INSERT Into SERVER (name, owner_id, logo_location) values ('test_server', 1, 'logo1.png');

INSERT Into SERVER_DISCORD_USERS (servers_id, discord_users_id) values (1, 1);