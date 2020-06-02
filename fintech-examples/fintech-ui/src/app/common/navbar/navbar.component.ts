import { AfterViewInit, Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { SimpleTimer } from 'ng2-simple-timer';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements AfterViewInit {
  private static TIMER_NAME = 'TIMER_NAME';
  expired = false;

  sessionValidUntil = '';
  redirectsValidUntil = Array.from(new Array<string>());

  constructor(
    private simpleTimer: SimpleTimer,
    private authService: AuthService,
    private router: Router
  ) {
  }

  ngAfterViewInit(): void {
    console.log(new Date() + ' start timer');
    this.simpleTimer.newTimerCD(NavbarComponent.TIMER_NAME, 1, 1);
    this.simpleTimer.subscribe(NavbarComponent.TIMER_NAME, () => this.timerRings());
  }

  public timerRings(): void {
    if (!this.isLoggedIn()) {
      if (this.expired) {
        this.router.navigate(['/session-expired']);
        this.expired = false;
      }
      return;
    }
    this.expired = true;
    this.sessionValidUntil = this.getSessionValidUntilAsString(this.authService.getValidUntilDate());

    this.redirectsValidUntil = new Array<string>();
    for (const s of Array.from(this.authService.getRedirectMap().values())) {
      this.redirectsValidUntil.push(this.getSessionValidUntilAsString(new Date(s.validUntil)));
    }
  }

  onLogout() {
    this.authService.logout();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUserName(): string {
    return this.authService.getUserName();
  }

  private getSessionValidUntilAsString(validUntilDate: Date): string {
    if (validUntilDate !== null) {
      const now: number = Date.now().valueOf();
      const ses: number = validUntilDate.valueOf();
      let diff: number = Math.floor((ses - now) / 1000);
      if (diff < 0) {
        diff = 0;
      }

      return this.getTimerTimeAsString(diff);
    }
    return '';
  }

  private getTimerTimeAsString(value: number): string {
    const h: number = Math.floor(value / 3600);
    const m: number = Math.floor((value - h * 3600) / 60);
    const s: number = Math.floor(value - h * 3600 - m * 60);
    return (h > 0 ? ('00' + h).slice(-2) + ':' : '') + ('00' + m).slice(-2) + ':' + ('00' + s).slice(-2);
  }
}
