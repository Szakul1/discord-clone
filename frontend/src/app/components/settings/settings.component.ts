import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { DiscordUser } from 'src/app/interfaces/discordUser';
import { Server } from 'src/app/interfaces/server';
import { TextChannel } from 'src/app/interfaces/textChannel';
import { ServerService } from 'src/app/services/server.service';
import { TextChannelService } from 'src/app/services/text-channel.service';
import { UserService } from 'src/app/services/user.service';
import { CreateUser } from 'src/app/interfaces/create-user';
import { environment } from 'src/environments/environment';
import { UpdateType } from 'src/app/enums/updateType';
import { UpdateDto } from 'src/app/interfaces/update-dto';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit, OnChanges {
  @Input() server?: Server;
  @Input() users?: DiscordUser[];
  @Input() owner: boolean = false;
  @Input() channel?: TextChannel;
  @Input() user?: DiscordUser;
  @Output() closeSettings = new EventEmitter<UpdateDto>();
  confirmation: boolean = false;
  chosenSetting: number = 0;
  logo?: File // null - dont do anything with file, emptyFile - delete logo
  showLogo: boolean = false;
  showSubmit: boolean = false;

  showEditUsername: boolean = false;
  showEditEmail: boolean = false;
  showEditPassword: boolean = false;

  updateType: UpdateType = UpdateType.none;

  inviteToken?: string;

  constructor(private serverService: ServerService, private channelService: TextChannelService, private sanitize: DomSanitizer,
    private router: Router, private userService: UserService) {
  }

  ngOnInit(): void {
    if (this.server) {
      this.inviteToken = `${environment.url}/join?token=${this.server?.inviteToken}`
      if (this.server.logo !== null)
        this.showLogo = true;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.server) {
      this.users?.splice(this.users.findIndex(u => u.username === this.server?.owner), 1);
    }
  }

  editServer(editForm: NgForm) {
    let server: Server = { name: editForm.value.name };
    this.serverService.update(this.server!.id!, server, this.logo).subscribe(server => this.close(server, UpdateType.update));
  }

  editChannel(editForm: NgForm) {
    let channel: TextChannel = { name: editForm.value.name };
    this.channelService.update(this.channel!.id!, channel).subscribe(channel => this.close(channel, UpdateType.update));
  }

  deleteServer() {
    this.serverService.delete(this.server!.id!).subscribe(() => this.close(this.server, UpdateType.delete));
  }
  
  deleteChannel() {
    this.channelService.delete(this.channel!.id!).subscribe(() => this.close(this.channel, UpdateType.delete));
  }

  closeWithoutUpdate() {
    this.closeSettings.emit({object: undefined, updateType: this.updateType});
  }

  close(object: Server | TextChannel | DiscordUser | undefined, updateType: UpdateType) {
    this.closeSettings.emit({object: object, updateType: updateType});
  }

  uploadLogo(event: Event) {
    this.showLogo = true;
    this.showSubmit = true;
    this.logo = (event.target as HTMLInputElement).files![0];
  }

  uploadAvatar(event: Event) {
    this.userService.uploadAvatar((event.target as HTMLInputElement).files![0]).subscribe(user =>
      this.updateUser(user));
  }

  deleteAvatar() {
    this.userService.uploadAvatar(new File([], '')).subscribe(user =>
      this.updateUser(user));
  }

  deleteLogo() {
    this.showLogo = false;
    this.showSubmit = true;
    this.logo = new File([], '');
  }

  restoreForm(editForm: NgForm) {
    if (this.server!.logo !== null)
      this.showLogo = true;
    this.showSubmit = false;
    return editForm.reset({name: this.server!.name});
  }

  restoreChannelForm(editForm: NgForm) {
    return editForm.reset({name: this.channel!.name});
  }

  getImage(): SafeUrl {
    if (this.logo !== undefined && this.logo.size > 0)
      return this.sanitize.bypassSecurityTrustResourceUrl(URL.createObjectURL(this.logo));
    return 'data:image/jpeg;base64,' + this.server!.logo;
  }

  getAvatar(): SafeUrl {
    if (this.logo !== undefined && this.logo.size > 0)
      return this.sanitize.bypassSecurityTrustResourceUrl(URL.createObjectURL(this.logo));
    return 'data:image/jpeg;base64,' + this.user!.avatar;
  }

  logOut() {
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('password');
    this.router.navigateByUrl('login');
  }

  editUsername() {
    this.showEditUsername = true;
  }

  editEmail() {
    this.showEditEmail = true;
  }

  editPassword() {
    this.showEditPassword = true;
  }

  saveUsername(editForm: NgForm) {
    let username = editForm.value.username;
    const updateUser: CreateUser = { username: username };
    this.userService.updateUsername(updateUser).subscribe(user => {
      this.updateUser(user);
      this.showEditUsername = false;
      this.updateAuth(username, undefined);
    });
  }

  saveEmail(editForm: NgForm) {
    const updateUser: CreateUser = { email: editForm.value.email };
    this.userService.updateEmail(updateUser).subscribe(user => {
      this.updateUser(user);
      this.showEditEmail = false;
    });
  }

  savePassword(editForm: NgForm) {
    let password = editForm.value.password;
    const updateUser: CreateUser = { password: password };
    this.userService.updatePassword(updateUser).subscribe(user => {
      this.updateUser(user);
      this.showEditPassword = false;
      this.updateAuth(undefined, password);
    });
  }

  private updateUser(user: DiscordUser) {
    this.user = user;
    this.userService.user = user;
    sessionStorage.setItem('user', JSON.stringify(user)); // delete
    this.updateType = UpdateType.update;
  }

  private updateAuth(username?: string, password?: string) {
    if (username)
      sessionStorage.setItem('username', username);
    if (password)
      sessionStorage.setItem('password', password);
  }

  copyToClipboad() {
   navigator.clipboard.writeText(this.inviteToken!); 
  }

  showSetting(i: number) {
    this.chosenSetting = i;
  }

  banUser(user: DiscordUser) {
    this.serverService.banUser(this.server?.id!, user.id!).subscribe(() => {
      this.users?.splice(this.users.findIndex(u => u.id === user.id), 1);
    });
  }

  leaveServer() {
    this.serverService.leave(this.server?.id!).subscribe(() => this.close(this.server, UpdateType.none));
  }

}
