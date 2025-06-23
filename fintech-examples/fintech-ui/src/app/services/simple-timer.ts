import { Injectable } from '@angular/core';
import { Observable, Subscription, timer } from 'rxjs';
import { UUID } from 'angular2-uuid';

type TimerList = Record<
  string,
  {
    second: number;
    t: Observable<number>;
  }
>;

type SubscriptionList = Record<
  string,
  {
    name: string;
    subscription: Subscription;
  }
>;

@Injectable({ providedIn: 'root' })
export class SimpleTimer {
  private timers: TimerList = {};
  private subscription: SubscriptionList = {};

  getSubscription(): string[] {
    return Object.keys(this.subscription);
  }

  newTimerCD(name: string, sec: number, delay = 0): boolean {
    if (name === undefined || sec === undefined || this.timers[name]) {
      return false;
    }
    const t: Observable<number> = timer(delay * 1000, sec * 1000);
    this.timers[name] = { second: sec, t: t };
    return true;
  }

  subscribe(name: string, callback: () => void): string {
    if (!this.timers[name]) {
      return '';
    }
    const id = name + '-' + UUID.UUID();
    this.subscription[id] = {
      name: name,
      subscription: this.timers[name].t.subscribe(callback)
    };
    return id;
  }

  unsubscribe(id: string): boolean {
    if (!id || !this.subscription[id]) {
      return false;
    }
    this.subscription[id].subscription.unsubscribe();
    delete this.subscription[id];
  }
}
