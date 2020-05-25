import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'consent-app-enter-pin-page',
  templateUrl: './enter-pin-page.component.html',
  styleUrls: ['./enter-pin-page.component.scss']
})
export class EnterPinPageComponent implements OnInit {
  public static ROUTE = 'enter-pin';

  wrongPassword = false;

  constructor() {}

  ngOnInit() {}

  onSubmit(pin: string): void {}
}
