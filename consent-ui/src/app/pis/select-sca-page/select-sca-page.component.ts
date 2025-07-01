import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiHeaders } from '../../api/api.headers';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'consent-app-select-sca-page',
  templateUrl: './select-sca-page.component.html',
  styleUrls: ['./select-sca-page.component.scss'],
  standalone: false
})
export class SelectScaPageComponent implements OnInit {
  public static ROUTE = 'select-sca-method';

  authorizationSessionId: string;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.authorizationSessionId = this.route.parent.snapshot.paramMap.get('authId');
  }

  onSubmit(res: HttpResponse<unknown>): void {
    // redirect to the provided location
    console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
    window.location.href = res.headers.get(ApiHeaders.LOCATION);
  }
}
