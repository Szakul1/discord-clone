import {
  Component, EventEmitter,
  Input, OnInit,
  Output
} from '@angular/core';
import { NgForm } from '@angular/forms';
import { UpdateType } from 'src/app/enums/updateType';
import { DiscordUser } from 'src/app/interfaces/discordUser';
import { Server } from 'src/app/interfaces/server';
import { TextChannel } from 'src/app/interfaces/textChannel';
import { UpdateDto } from 'src/app/interfaces/update-dto';
import { TextChannelService } from 'src/app/services/text-channel.service';
import { UserService } from 'src/app/services/user.service';

enum Settings {
  server,
  channel,
  user,
  none
}

@Component({
  selector: 'app-server-display',
  templateUrl: './server-display.component.html',
  styleUrls: ['./server-display.component.css'],
})
export class ServerDisplayComponent implements OnInit {
  @Output() updateServer = new EventEmitter<UpdateDto>();
  @Input() set server (server: Server) {
    const newServer = server.id !== this.displayServer?.id;
    this.displayServer = server;
    if (newServer)
      this.init();
  };
  
  displayServer!: Server;
  channels: TextChannel[] = [];
  users: DiscordUser[] = [];
  displayChannel?: TextChannel;
  currentUser!: DiscordUser;
  
  avatarCache: { [key: string]: string } = {};
  creatingChannel: boolean = false;
  
  settingsType = Settings;
  currentSetting: Settings = Settings.none;

  constructor(private textChannelService: TextChannelService,
    private userService: UserService) {}

  ngOnInit(): void {
    this.init();
    // this.currentUser = this.userService.user!;
    this.currentUser = JSON.parse(sessionStorage.getItem('user')!);
  }

  init() {
    this.channels = [];
    this.users = [];
    this.displayChannel = undefined;
    this.avatarCache = {};

    this.userService.getUsers(this.displayServer.id!).subscribe(users => {
      this.users = users;
      users.forEach(u => {
        this.avatarCache[u.username] = u.avatar!;
      });
      this.getChannels();
    });
  }

  createTextChannel(addForm: NgForm) {
    this.textChannelService
      .create(this.displayServer.id!, (addForm.value.name as string).trim())
      .subscribe(channel => {
        this.channels.push(channel);
        this.displayChannel = channel;
      });
    addForm.reset();
    this.creatingChannel = false;
  }

  showChannel(channel: TextChannel) {
    this.displayChannel = channel;
  }

  getAvatar(user: DiscordUser): string {
    return 'data:image/jpeg;base64,' + user.avatar;
  }

  openServerSettings() {
    this.currentSetting = Settings.server;
  }

  openChannelSettings() {
    this.currentSetting = Settings.channel;
  }

  openUserSettings() {
    this.currentSetting = Settings.user;
  }

  closeSettings(updateDto: UpdateDto) {
    if (updateDto.updateType !== UpdateType.none) {
      switch (this.currentSetting) {
        case Settings.channel:
          const channel = updateDto.object as TextChannel;
          if (updateDto.updateType == UpdateType.update) {
            this.displayChannel = channel;
            this.channels[this.channels.findIndex(c => c.id === channel.id)] = channel;
          } else {
            this.displayChannel = undefined;
            this.channels.splice(this.channels.findIndex(c => c.id === channel.id), 1);
          }
          break;
        case Settings.server:
          this.updateServer.emit(updateDto);
          break;
        case Settings.user:
          // this.currentUser = this.userService.user!;
          this.currentUser = JSON.parse(sessionStorage.getItem('user')!);
          break;
      }
    }
    this.currentSetting = Settings.none;
  }

  getImage() {
    return 'data:image/jpeg;base64,' + this.currentUser.avatar;
  }

  private getChannels() {
    this.textChannelService.getChannels(this.displayServer.id!).subscribe(channels => {
      this.channels = channels;
      if (channels.length > 0) {
        this.showChannel(channels[0]);
      }
    });
  }

}
