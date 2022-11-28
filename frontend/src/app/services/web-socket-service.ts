import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';
import * as Stomp from 'stompjs';
import { Message } from '../interfaces/message';

export class WebSocketService {
  private stompClient?: Stomp.Client;

  constructor(private channelId: number,
     private create: Function,
     private update: Function,
     private deletion: Function) {
    this.connect();
  }

  private connect() {
    const socket = new SockJS(environment.baseUrl + '/chat');
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {};
    const username = sessionStorage.getItem('username');
    const password = sessionStorage.getItem('password');

    this.stompClient.connect({password: password, username: username}, (frame) => {
        this.stompClient?.subscribe(
        `/start/create/${this.channelId}`,
        (message) => {
            let m: Message = JSON.parse(message.body);
            this.create(m);
          }
        );

        this.stompClient?.subscribe(
        `/start/update/${this.channelId}`,
        (message) => {
            let m: Message = JSON.parse(message.body);
            this.update(m);
          }
        );

        this.stompClient?.subscribe(
        `/start/delete/${this.channelId}`,
        (message) => {
            let messageId: number = Number(message.body);
            this.deletion(messageId);
          }
        );
    });
  }

  sendMessage(message: string) {
    this.stompClient?.send(
        `/current/create/${this.channelId}`,
        {},
        message
    );
  }

  updateMessage(message: Message) {
    this.stompClient?.send(
        `/current/update/${this.channelId}`,
        {},
        JSON.stringify(message)
    );
  }

  deleteMessage(messageId: number) {
    this.stompClient?.send(
        `/current/delete/${this.channelId}`,
        {},
        String(messageId)
    );
  }

  disconnect() {
    this.stompClient?.disconnect(() => {}, {});
  }

}