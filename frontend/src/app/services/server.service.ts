import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Server } from '../interfaces/server';

@Injectable({
  providedIn: 'root'
})
export class ServerService {
  private serverUrl = environment.baseUrl + '/server'

  constructor(private http: HttpClient) { }

  joinServer(token: string): Observable<void> {
    return this.http.get<void>(`${this.serverUrl}/join`, {
      params: {
        token: token
      }
    });
  }

  getByToken(token: string): Observable<Server> {
    return this.http.get<Server>(`${this.serverUrl}/get`, {
      params: {
        token: token
      }
    });
  }
  
  getUserServers(): Observable<Server[]> {
    return this.http.get<Server[]>(this.serverUrl);
  }

  create(server: Server, logo?: File): Observable<Server> {
    return this.http.post<Server>(this.serverUrl, this.creatForm(server, logo));
  }

  update(id: number, server: Server, logo?: File): Observable<Server> {
    return this.http.put<Server>(`${this.serverUrl}/${id}`, this.creatForm(server, logo));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.serverUrl}/${id}`);
  }

  banUser(serverId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.serverUrl}/ban/${serverId}/${userId}`);
  }
  
  leave(serverId: number): Observable<void> {
    return this.http.delete<void>(`${this.serverUrl}/leave/${serverId}`);
  }

  private creatForm(server: Server, logo?: File) {
    let formData = new FormData();
    if (logo !== undefined)
      formData.append('logo', logo);
    formData.append('server', new Blob([JSON.stringify(server)], {type: 'application/json'}));
    formData.forEach(a => console.log(a));
    return formData;
  }

}
