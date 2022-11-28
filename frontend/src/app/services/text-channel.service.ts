import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { TextChannel } from '../interfaces/textChannel';

@Injectable({
  providedIn: 'root'
})
export class TextChannelService {
  private textChannelUrl = environment.baseUrl + '/textChannel'
  
  constructor(private http: HttpClient) { }

  getChannels(serverId: number): Observable<TextChannel[]> {
    return this.http.get<TextChannel[]>(`${this.textChannelUrl}/${serverId}`);
  }
  
  create(serverId: number, name: string): Observable<TextChannel> {
    let textChannel: TextChannel = { name: name };
    return this.http.post<TextChannel>(`${this.textChannelUrl}/${serverId}`, textChannel);
  }
  
  update(id: number, channel: TextChannel): Observable<TextChannel> {
    return this.http.put<TextChannel>(`${this.textChannelUrl}/${id}`, channel);
  }
  
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.textChannelUrl}/${id}`);
  }
}
