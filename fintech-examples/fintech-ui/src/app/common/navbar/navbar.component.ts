import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { StorageService } from '../../services/storage.service';
import { toLocaleString } from '../../models/consts';
import { RedirectTupelForMap } from '../../bank/redirect-page/redirect-struct';
import { SimpleTimer } from 'ng2-simple-timer';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  private static TIMER_NAME = 'TIMER_NAME';
  private sessionValidUntil = '';
  private redirectsValidUntil = Array.from(new Array<string>());

  constructor(
    private simpleTimer: SimpleTimer,
    private authService: AuthService,
    private router: Router,
    private storageService: StorageService
  ) {}

  ngOnInit() {
    this.simpleTimer.newTimerCD(NavbarComponent.TIMER_NAME, 1, 2);
    this.simpleTimer.subscribe(NavbarComponent.TIMER_NAME, () => this.timerRings());
  }

  public timerRings(): void {
    if (!this.isLoggedIn()) {
      console.log('user is no more logged in');
      this.storageService.clearStorage();
      this.router.navigate(['/login']);
    }
    this.sessionValidUntil = this.getSessionValidUntilAsString(this.storageService.getValidUntilDate());

    this.redirectsValidUntil = new Array<string>();
    for (const s of Array.from(this.storageService.getRedirectMap().values())) {
      this.redirectsValidUntil.push(this.getSessionValidUntilAsString(new Date(s.validUntil)));
    }
  }

  onLogout() {
    this.authService.logout();
  }

  isLoggedIn(): boolean {
    return this.storageService.isLoggedIn();
  }

  getUserName(): string {
    return this.storageService.getUserName();
  }

  private getSessionValidUntilAsString(validUntilDate: Date): string {
    if (validUntilDate !== null) {
      const validUntilDateString = toLocaleString(validUntilDate);
      const regEx = /.*([0-9]{2}:[0-9]{2}:[0-9]{2})/;
      const matches = validUntilDateString.match(regEx);
      if (matches.length !== 2) {
        throw new Error('valid until is not parsable ' + validUntilDateString);
      }
      // return matches[1];

      const now: number = Date.now().valueOf();
      const ses: number = validUntilDate.valueOf();
      let diff: number = Math.floor((ses - now) / 1000);
      if (diff < 0) {
        diff = 0;
      }
      return '' + diff;
    }
    console.log('validUntilDate is NULL');
    return '';
  }
}
