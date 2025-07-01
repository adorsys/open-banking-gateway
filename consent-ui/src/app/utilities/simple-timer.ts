import { Injectable } from '@angular/core';
import { Observable, Subscription, timer } from 'rxjs';
import { UUID } from 'angular2-uuid';

type TimerValue = number;

interface TimerList {
  [name: string]: {
    second: number;
    t: Observable<TimerValue>;
  };
}

interface SubscriptionList {
  [id: string]: {
    name: string;
    subscription: Subscription;
  };
}

@Injectable({ providedIn: 'root' })
export class SimpleTimer {
  private timers: TimerList = {};
  private subscription: SubscriptionList = {};

  getTimer(): string[] {
    return Object.keys(this.timers);
  }
  getSubscription(): string[] {
    return Object.keys(this.subscription);
  }
  newTimer(name: string, sec: number, delay: boolean = false): boolean {
    if (name === undefined || sec === undefined || this.timers[name]) {
      return false;
    }
    let t: Observable<TimerValue>;
    if (delay) {
      t = timer(sec * 1000, sec * 1000);
    } else {
      t = timer(0, sec * 1000);
    }
    this.timers[name] = { second: sec, t: t };
    return true;
  }
  newTimerCD(name: string, sec: number, delay: number = 0): boolean {
    if (name === undefined || sec === undefined || this.timers[name]) {
      return false;
    }
    let t: Observable<TimerValue>;
    t = timer(delay * 1000, sec * 1000);
    this.timers[name] = { second: sec, t: t };
    return true;
  }
  newTimerHR(name: string, msec: number, delay: number = 0): boolean {
    if (name === undefined || msec === undefined || this.timers[name]) {
      return false;
    }
    let t: Observable<TimerValue>;
    t = timer(delay, msec);
    this.timers[name] = { second: msec, t: t };
    return true;
  }
  delTimer(name: string): boolean {
    if (name === undefined || !this.timers[name]) {
      return false;
    }
    let s = this.getSubscription();
    // unsubscribe all subscription for queue 'name'
    s.forEach((i) => {
      if (this.subscription[i].name === name) {
        this.unsubscribe(i);
      }
    });
    // delete queue 'name' subject and observable
    delete this.timers[name].t;
    delete this.timers[name];
  }
  /**
   *
   * @param name
   * @param callback
   */
  subscribe(name: string, callback: () => void): string {
    if (!this.timers[name]) {
      return '';
    }
    let id = name + '-' + UUID.UUID();
    this.subscription[id] = {
      name: name,
      subscription: this.timers[name].t.subscribe(callback)
    };
    return id;
  }
  /**
   *
   * @param id
   */
  unsubscribe(id: string): boolean {
    if (!id || !this.subscription[id]) {
      return false;
    }
    this.subscription[id].subscription.unsubscribe();
    delete this.subscription[id];
  }
}
