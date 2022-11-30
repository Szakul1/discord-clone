import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Settings } from 'src/app/enums/settings';
import { UpdateType } from 'src/app/enums/updateType';
import { DiscordUser } from 'src/app/interfaces/discordUser';
import { Server } from 'src/app/interfaces/server';
import { UpdateDto } from 'src/app/interfaces/update-dto';
import { ServerService } from 'src/app/services/server.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css'],
})
export class HomePageComponent implements OnInit {
  servers: Server[] = [];
  displayServer?: Server;
  isCreating: boolean = false;
  logo?: File;

  currentUser!: DiscordUser;
  settingsType = Settings;
  currentSetting: Settings = Settings.none;

  constructor(private serverService: ServerService, private sanitize: DomSanitizer) {}

  ngOnInit(): void {
    this.getUserServers();
    // this.currentUser = this.userService.user!;
    this.currentUser = JSON.parse(sessionStorage.getItem('user')!);
  }

  getUserServers() {
    this.serverService.getUserServers().subscribe((servers) => {
      this.servers = servers;
      if (!this.displayServer) {
        if (servers.length > 0) {
          this.displayServer = this.servers[0];
        } else {
          this.displayServer = undefined;
        }
      }
    });
  }

  create(addForm: NgForm) {
    let server: Server = { name: (addForm.value.name as string).trim() };
    this.serverService.create(server, this.logo).subscribe(server => {
      this.servers.push(server);
      this.displayServer = server;
    });
    addForm.reset();
    this.resetForm();
  }

  showServer(server?: Server) {
    this.displayServer = server;
  }

  updateServer(updateDto: UpdateDto) {
    let server = updateDto.object as Server;
    if (updateDto.updateType === UpdateType.update) {
      this.displayServer = server;
      this.servers[(this.servers.findIndex(s => s.id === server.id))] = server;
    } else {
      this.displayServer = undefined
      this.servers.splice(this.servers.findIndex(s => s.id === server.id), 1);
      if (this.servers.length > 0)
        this.showServer(this.servers[0]);
    }
  }

  uploadLogo(event: Event) {
    this.logo = (event.target as HTMLInputElement).files![0];
  }

  getLogo(): SafeUrl {
    if (this.logo)
      return this.sanitize.bypassSecurityTrustResourceUrl(URL.createObjectURL(this.logo));
    
    return '';
  }

  resetForm() {
    this.isCreating = false;
    this.logo = undefined;
  }

  isOwner() {
    return this.currentUser.username === this.displayServer?.owner;
  }

  getImage() {
    return 'data:image/jpeg;base64,' + this.currentUser.avatar;
  }

  openUserSettings() {
    this.currentSetting = Settings.user;
  }

  closeSettings(updateDto: UpdateDto) {
    // this.currentUser = this.userService.user!;
    this.currentUser = JSON.parse(sessionStorage.getItem('user')!);
    this.currentSetting = Settings.none;
  }
  
}
