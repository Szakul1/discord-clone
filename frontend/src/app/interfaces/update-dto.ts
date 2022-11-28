import { UpdateType } from "../enums/updateType";
import { DiscordUser } from "./discordUser";
import { Server } from "./server";
import { TextChannel } from "./textChannel";

export interface UpdateDto {
    object?: Server | TextChannel | DiscordUser;
    updateType: UpdateType;
}