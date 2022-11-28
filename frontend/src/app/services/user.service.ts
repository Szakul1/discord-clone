import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CreateUser } from '../interfaces/create-user';
import { DiscordUser } from '../interfaces/discordUser';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userUrl = environment.baseUrl + '/user'
  user?: DiscordUser;
  
  constructor(private http: HttpClient) { }
  
  getUsers(serverId: number): Observable<DiscordUser[]> {
    return this.http.get<DiscordUser[]>(`${this.userUrl}/${serverId}`);
  }

  create(createUser: CreateUser): Observable<DiscordUser> {
    return this.http.post<DiscordUser>(`${this.userUrl}/register`, createUser);
  }
  
  confirmEmail(token: string): Observable<void> {
    return this.http.get<void>(`${this.userUrl}/confirm`, {
      params: {
        token: token
      }
    })
  }

  resendToken(user: CreateUser): Observable<void> {
    return this.http.put<void>(`${this.userUrl}/resend`, user);
  }

  resendEmail(email: string): Observable<void> {
    return this.http.put<void>(`${this.userUrl}/resendEmail`, {
      params: {
        email: email
      }
    })
  }

  getAvatar(username: string): Observable<string> {
    return this.http.get<string>(`${this.userUrl}/avatar/${username}`);
  }

  uploadAvatar(file: File): Observable<DiscordUser> {
    return this.http.post<DiscordUser>(`${this.userUrl}/avatar`, this.creatForm(file));
  }

  updateUsername(updateUser: CreateUser): Observable<DiscordUser> {
    return this.http.put<DiscordUser>(`${this.userUrl}/username`, updateUser);
  }

  updateEmail(updateUser: CreateUser): Observable<DiscordUser> {
    return this.http.put<DiscordUser>(`${this.userUrl}/email`, updateUser);
  }

  updatePassword(updateUser: CreateUser): Observable<DiscordUser> {
    return this.http.put<DiscordUser>(`${this.userUrl}/password`, updateUser);
  }

  private creatForm(avatar: File): FormData {
    let formData = new FormData();
    formData.append('avatar', avatar);
    return formData;
  }

}
