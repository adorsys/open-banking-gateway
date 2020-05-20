import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'consent-app-enter-tan-page',
  templateUrl: './enter-tan-page.component.html',
  styleUrls: ['./enter-tan-page.component.scss']
})
export class EnterTanPageComponent implements OnInit {
  public static ROUTE = 'enter-tan';

  wrongSca = false;
  constructor() {}

  ngOnInit() {}

  onSubmit(tan: string): void {}
}
