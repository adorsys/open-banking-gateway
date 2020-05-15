import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'consent-app-enter-sca-page',
  templateUrl: './enter-sca-page.component.html',
  styleUrls: ['./enter-sca-page.component.scss']
})
export class EnterScaPageComponent implements OnInit {
  public static ROUTE = 'enter-sca';

  wrongSca = false;
  constructor() {}

  ngOnInit() {}

  onSubmit(sca: string): void {}
}
