import { DiscordUser } from "./discordUser"

export interface Message {
    id?: number
    message: string
    author?: string
    creationDate?: Date
    edited?: boolean
}