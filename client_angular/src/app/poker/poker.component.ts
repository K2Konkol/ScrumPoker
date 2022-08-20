import { Component, OnInit, OnDestroy } from '@angular/core';
import { DataService } from '../data.service';
import {catchError, distinctUntilChanged, EMPTY, map, reduce, shareReplay, tap} from 'rxjs';
import { Message } from '../message'
import { DECK } from "../deck";

@Component({
  selector: 'app-poker',
  templateUrl: './poker.component.html',
  styleUrls: ['./poker.component.css']
})
export class PokerComponent implements OnInit, OnDestroy {

  player?: string
  rank?: number
  score? : number
  deck: number[] = DECK;

  constructor(private service: DataService) {
  }

  play$ = this.service.dataUpdates()
    .pipe(shareReplay(),
      distinctUntilChanged((p,c) => JSON.stringify(p) === JSON.stringify(c)),
      tap(txt => console.log(JSON.stringify(txt))),
      catchError(err => {
        console.log(err);
        return EMPTY;
      })
    )

  ready$ = this.play$.pipe(
    map(msg => {
      let msgList: Message[] = msg;
      // @ts-ignore
      return msgList.length > 0 && msgList.every(e => e.rank > 0)
      }),
    tap(e => console.log(e)),
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
    let messageToSend = {player: this.player, rank: this.rank} as Message;
    this.service.sendMessage(messageToSend);
    console.log(`Sending ${messageToSend.player}`)
  }

  onSelect(value: number):void {
    this.rank = this.rank === undefined || this.rank !== value ? value : undefined;
    this.updateStatus();
  }
}
