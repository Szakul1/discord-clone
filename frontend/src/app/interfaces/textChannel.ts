import { Message } from "./message"

export interface TextChannel {
    id?: number;
    name: string;
    messages?: Message[];
}