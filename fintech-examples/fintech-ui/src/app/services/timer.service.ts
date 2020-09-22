import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { SimpleTimer } from 'ng2-simple-timer';
import { BehaviorSubject } from 'rxjs';
import { AuthService } from './auth.service';
import { TimerModel } from '../models/timer.model';
import { RoutingPath } from '../models/routing-path.model';

@Injectable({
  providedIn: 'root'
})
export class TimerService {
  private TIMER_NAME = 'TIMER_NAME';
  private timer = new TimerModel();

  private timerSubject$ = new BehaviorSubject<TimerModel>(this.timer);
  timerStatusChanged$ = this.timerSubject$.asObservable();

  constructor(private simpleTimer: SimpleTimer, private authService: AuthService, private router: Router) {}

  public stopTimer(): void {
    this.simpleTimer.unsubscribe(this.simpleTimer.getSubscription().toString());
  }

  public startTimer(): void {
    this.simpleTimer.newTimerCD(this.TIMER_NAME, 1, 1);
    this.simpleTimer.subscribe(this.TIMER_NAME, () => this.timerRings());
  }

  private timerRings(): void {
    if (!this.authService.isLoggedIn()) {
      this.stopTimer();
      if (this.router.url !== `/${RoutingPath.LOGIN}`) {
        this.router.navigate([RoutingPath.SESSION_EXPIRED]);
      }
    } else {
      const sessionValidUntil = this.getSessionValidUntilAsString(this.authService.getValidUntilDate());
      const redirectsValidUntil = new Array<string>();

      for (const s of Array.from(this.authService.getRedirectMap().values())) {
        redirectsValidUntil.push(this.getSessionValidUntilAsString(new Date(s.validUntil)));
      }

      this.timer = { started: true, sessionValidUntil, redirectsValidUntil };
      this.timerSubject$.next(this.timer);
    }
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
