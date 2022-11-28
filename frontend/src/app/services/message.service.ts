import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Message } from '../interfaces/message';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private messageUrl = environment.baseUrl + '/message'

  constructor(private http: HttpClient) { }

  getMessages(textChannelId: number, page: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.messageUrl}/${textChannelId}`, {
      params: {
        page: page
      }
    });
  }

  update(id: number, message: Message): Observable<Message> {
    return this.http.put<Message>(`${this.messageUrl}/${id}`, message)
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.messageUrl}/${id}`)
  }

}
