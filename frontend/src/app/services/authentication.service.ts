import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { DiscordUser } from '../interfaces/discordUser';
import { CreateUser } from '../interfaces/create-user';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private serverUrl = environment.baseUrl + '/auth'
  
  constructor(private http: HttpClient) { }

  authenticate(username: string, password: string): Observable<DiscordUser> {
    const user: CreateUser = {username: username, password: password}
    return this.http.post<DiscordUser>(this.serverUrl, user);
  }

}
