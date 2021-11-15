import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'consent-app-restore-session-page',
  templateUrl: './restore-session-page.component.html',
  styleUrls: ['./restore-session-page.component.scss']
})
export class RestoreSessionPageComponent implements OnInit {

  public static ROUTE = 'restore-session';

  constructor() { }

  ngOnInit(): void {
  }

}
