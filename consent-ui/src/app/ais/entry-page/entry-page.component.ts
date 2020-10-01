import { Component, OnInit } from '@angular/core';
import { CookieRenewalService } from '../common/cookie-renewal/CookieRenewalService';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'consent-app-entry-page',
  templateUrl: './entry-page.component.html',
  styleUrls: ['./entry-page.component.scss']
})
export class EntryPageComponent implements OnInit {
  private authid;
  constructor(private route: ActivatedRoute, private cookieRenewalService: CookieRenewalService) {}

  ngOnInit() {
    console.log('EntryPageComponent onInit');
    this.route.paramMap.subscribe((p) => {
      this.authid = p.get('authId');
      this.cookieRenewalService.activate(this.authid);
    });
  }
}
