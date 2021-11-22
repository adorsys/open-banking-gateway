import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot} from "@angular/router";

@Component({
  selector: 'consent-app-restore-session-page',
  templateUrl: './restore-session-page.component.html',
  styleUrls: ['./restore-session-page.component.scss']
})
export class RestoreSessionPageComponent implements OnInit {

  // ING does not support query parameters, so everything must be in URL path
  public static ROUTE = 'restore-session/:aspspRedirectCode/:state';

  private route: ActivatedRouteSnapshot;
  authId: string;
  aspspRedirectCode: string;
  result: string;
  oauthCode: string;

  constructor(
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.route = this.activatedRoute.snapshot

    this.authId = this.route.parent.params.authId;
    this.aspspRedirectCode = this.route.params.aspspRedirectCode;
    this.result = this.route.params.state;
    this.oauthCode = this.route.queryParams.code;
  }
}
