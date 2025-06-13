import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'consent-app-error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss'],
  standalone: false
})
export class ErrorPageComponent implements OnInit {
  static ERROR_QUERY_MESSAGE = 'message';

  errorMessage = 'Unexpected error has occurred';

  constructor(private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((it) => {
      if (it[ErrorPageComponent.ERROR_QUERY_MESSAGE] && '' !== it[ErrorPageComponent.ERROR_QUERY_MESSAGE]) {
        this.errorMessage = it[ErrorPageComponent.ERROR_QUERY_MESSAGE];
      }
    });
  }
}
