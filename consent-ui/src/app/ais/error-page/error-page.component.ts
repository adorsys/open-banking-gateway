import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'consent-app-error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss']
})
export class ErrorPageComponent implements OnInit {

  @Input() errorMessage = 'Unexpected error has occurred';

  constructor() { }

  ngOnInit() {
  }
}
