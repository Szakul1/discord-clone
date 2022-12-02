mc alias set rminio "$1" "$2" "$3"; # url, user, secret_key
mc mb rminio/discord;
mc cp avatar1.png rminio/discord/avatars/avatar1.png
