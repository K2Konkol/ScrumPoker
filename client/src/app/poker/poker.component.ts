import { Component, OnInit, OnDestroy } from '@angular/core';
import { DataService } from '../data.service';
import {catchError, EMPTY, flatMap, map, Observable, tap} from 'rxjs';
import { Message } from '../message'

@Component({
  selector: 'app-poker',
  templateUrl: './poker.component.html',
  styleUrls: ['./poker.component.css']
})
export class PokerComponent implements OnInit, OnDestroy {

  constructor(private service: DataService) {
  }

  play$ = this.service.dataUpdates()
    .pipe(
      tap(txt => console.log(JSON.stringify(txt))),
      catchError(err => {
        console.log(err);
        return EMPTY;
      })
    )

  ngOnDestroy(): void {
      this.service.closeConnection();
  }

  ngOnInit(): void {}

  updateStatus() {
    let messageToSend = {player: "Krzysiek", rank: 3} as Message;
    this.service.sendMessage(messageToSend);
    console.log(`Sending ${messageToSend.player}`)
  }

}
