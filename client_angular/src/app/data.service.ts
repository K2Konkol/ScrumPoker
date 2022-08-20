import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from "rxjs/webSocket";
import { Observable, timer, Subject, EMPTY, tap, delayWhen, switchAll, catchError} from "rxjs";
import { Message } from "./message"

@Injectable({
  providedIn: 'root'
})

export class DataService {
  private socket$: WebSocketSubject<Message[]> = webSocket('ws://localhost:8080/play');

  constructor() { }

  public dataUpdates() {
    return this.socket$.asObservable();
  }

  closeConnection() {
    return this.socket$.complete();
  }

  sendMessage(msg: any) {
    this.socket$.next(msg);
  }
}
