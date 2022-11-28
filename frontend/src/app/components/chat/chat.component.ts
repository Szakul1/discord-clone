import { AfterViewInit, Component, ElementRef, Input, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { Message } from 'src/app/interfaces/message';
import { TextChannel } from 'src/app/interfaces/textChannel';
import { MessageService } from 'src/app/services/message.service';
import { UserService } from 'src/app/services/user.service';
import { WebSocketService } from 'src/app/services/web-socket-service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, AfterViewInit {
  @ViewChild('messageEdit') set messageEdit(el: ElementRef) {
    if (el) {
      el.nativeElement.style.height = el.nativeElement.scrollHeight + "px";
    }
  };

  @ViewChild('messageScroll') scroll!: ElementRef;
  @ViewChildren('content') content!: QueryList<any>;

  @Input() avatarCache!: { [key: string]: string };
  @Input() set displayChannel(displayChannel: TextChannel) {
    this.channel = displayChannel;
    this.init();
  };
  
  channel?: TextChannel;
  messages: Message[] = [];
  messageToDelete?: Message;
  messageToEdit?: Message;

  webSocket?: WebSocketService;

  private scrollPosition?: number;
  private autoScroll: boolean = true;
  private loadedAll: boolean = false;
  private page: number = 0;

  constructor(private messageService: MessageService, private userService: UserService) {}

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    let scroll: HTMLElement = this.scroll.nativeElement;
    this.content.changes.subscribe(() => {
      if (this.autoScroll)
        scroll.scrollTop = scroll.scrollHeight;
      else if (this.scrollPosition) {
        scroll.scrollTop = scroll.scrollHeight - this.scrollPosition;
      }
    });
  }

  init() {
    this.messages = [];
    this.messageToDelete = undefined;
    this.messageToEdit = undefined;
    this.scrollPosition = undefined;
    this.autoScroll= true;
    this.loadedAll = false;
    this.page = 0;
    
    this.webSocket?.disconnect();
    this.messageService.getMessages(this.channel!.id!, this.page).subscribe(messages => {
      this.messages = messages;
      this.cacheAvatars(messages);
    });
    this.webSocket = new WebSocketService(this.channel!.id!, this.recieveMessage.bind(this),
      this.recieveUpdate.bind(this), this.recieveDelete.bind(this));
  }

  getAvatar(username: string): string {
    return 'data:image/jpeg;base64,' + this.avatarCache[username];
  }

  sendMessage(input: HTMLTextAreaElement) {
    var message = input.value.trim();
    input.value = '';
    if (message.length === 0)
      return;
    this.webSocket?.sendMessage(message);
  }

  recieveMessage(message: Message) {
    this.messages.push(message);
  }

  recieveUpdate(message: Message) {
    for (var i = 0; i < this.messages.length; i++) {
      if (this.messages[i].id === message.id) {
        this.messages[i] = message;
        break;
      }
    }
  }

  recieveDelete(messageId: number) {
    for (var i = 0; i < this.messages.length; i++) {
      if (this.messages[i].id === messageId) {
        break;
      }
    }
  }

  convertDate(date: Date): string {
    date = new Date(date);
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
  }

  prepareDeletion(message: Message) {
    this.messageToDelete = message;
  }

  deleteMessage() {
    this.webSocket?.deleteMessage(this.messageToDelete?.id!);
    this.messageToDelete = undefined;
  }

  prepareEdition(message: Message) {
    this.messageToEdit = message;
  }

  editMessage(message: string) {
    message = message.trim();
    const messageDto: Message = {
      id: this.messageToEdit?.id,
      message: message
    }
    if (message.length > 0)
      this.webSocket?.updateMessage(messageDto);
    this.messageToEdit = undefined;
  }

  rememberScroll() {
    let scroll: HTMLElement = this.scroll.nativeElement;
    this.autoScroll = scroll.offsetHeight + scroll.scrollTop >= scroll.scrollHeight - 1;
    this.scrollPosition = undefined;
    if (!this.loadedAll && scroll.scrollTop === 0) {
      this.scrollPosition = scroll.scrollHeight;
      this.page++;
      this.messageService.getMessages(this.channel!.id!, this.page).subscribe(messages => {
        if (messages.length === 0) {
          this.loadedAll = true;
          return;
        }
        this.cacheAvatars(messages);
        this.messages = messages.concat(this.messages);
      });
    }
  }

  private cacheAvatars(messages: Message[]) {
    messages.forEach(message => {
      if (!this.avatarCache[message.author!]) {
        this.userService.getAvatar(message.author!)
          .subscribe(avatar => this.avatarCache[message.author!] = avatar);
      }
    });
  }

}
