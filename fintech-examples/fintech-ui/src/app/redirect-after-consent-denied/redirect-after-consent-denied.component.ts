import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-redirect-after-consent-denied',
  templateUrl: './redirect-after-consent-denied.component.html',
  styleUrls: ['./redirect-after-consent-denied.component.scss']
})
export class RedirectAfterConsentDeniedComponent implements OnInit {
  constructor(private router: Router) {}

  // TODO: call to fintech-server to reset redirect cookie and to consent initiation data. Not available now in the Fintech-server
  ngOnInit() {}

  toBankSearch(): void {
    this.router.navigate(['/']);
  }
}
