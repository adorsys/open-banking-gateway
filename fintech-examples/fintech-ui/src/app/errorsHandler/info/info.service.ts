import { Injectable } from '@angular/core';
import { ComponentPortal } from '@angular/cdk/portal';
import { Overlay, OverlayRef } from '@angular/cdk/overlay';

import { InfoOptions } from './info-options';
import { InfoComponent } from './info.component';

@Injectable({
  providedIn: 'root'
})
export class InfoService {
  private overlayRef: OverlayRef;
  private feedbackComp: InfoComponent;
  private readonly CORNER_OFFSET = '20px';
  private readonly DEFAULT_OPTIONS: InfoOptions = {
    severity: 'info',
    closable: true,
    duration: 10000
  };

  constructor(private overlay: Overlay) {
    this.overlayRef = this.overlay.create({
      hasBackdrop: false,
      scrollStrategy: this.overlay.scrollStrategies.noop(),
      positionStrategy: this.overlay.position().global().right(this.CORNER_OFFSET).top(this.CORNER_OFFSET)
    });
  }

  openFeedback(message: string, options?: Partial<InfoOptions>) {
    if (this.overlayRef.hasAttached()) {
      this.overlayRef.detach();
    }
    const portal = new ComponentPortal(InfoComponent);
    const componentRef = this.overlayRef.attach(portal);
    this.feedbackComp = componentRef.instance;
    this.feedbackComp.open(message, { ...this.DEFAULT_OPTIONS, ...options });
    this.feedbackComp.onDestroy$.subscribe(() => {
      this.overlayRef.detach();
    });
  }
}
