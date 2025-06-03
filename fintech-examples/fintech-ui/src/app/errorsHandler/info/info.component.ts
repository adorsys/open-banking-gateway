import { Component, ViewEncapsulation } from '@angular/core';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { Subject } from 'rxjs';

import { InfoOptions } from './info-options';
import { NgClass, NgIf } from '@angular/common';
import Timeout = NodeJS.Timeout;

@Component({
  selector: 'app-feedback',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss'],
  animations: [
    trigger('feedbackAnimation', [
      state(
        'void',
        style({
          transform: 'translateY(100%)',
          opacity: 0
        })
      ),
      state(
        '*',
        style({
          transform: 'translateY(0)',
          opacity: 1
        })
      ),
      transition('* <=> void', animate(`400ms cubic-bezier(0.4, 0, 0.1, 1)`))
    ])
  ],
  encapsulation: ViewEncapsulation.None,
  standalone: true,
  imports: [NgClass, NgIf]
})
export class InfoComponent {
  message: string;
  options: InfoOptions;
  animationState: '*' | 'void' = 'void';
  private onDestroy = new Subject<void>();
  onDestroy$ = this.onDestroy.asObservable();
  private durationTimeoutId: Timeout;

  open(message: string, options: InfoOptions): void {
    this.message = message;
    this.options = options;
    this.animationState = '*';
  }

  animateClose(): void {
    this.animationState = 'void';
    clearTimeout(this.durationTimeoutId);
  }

  /**
   * This is called after the animation is done by Angular
   * The state decides whether the component should be destroyed or not
   */
  animationDone() {
    if (this.animationState === 'void') {
      this.onDestroy.next();
    } else if (this.animationState === '*') {
      if (this.options) {
        this.dismissAfter(this.options.duration);
      }
    }
  }

  private dismissAfter(duration: number): void {
    if (duration && duration > 0) {
      this.durationTimeoutId = setTimeout(() => this.animateClose(), duration);
    }
  }
}
