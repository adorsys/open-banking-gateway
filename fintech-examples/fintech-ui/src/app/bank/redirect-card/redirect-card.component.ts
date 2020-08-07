import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-redirect-card',
  templateUrl: './redirect-card.component.html',
  styleUrls: ['./redirect-card.component.scss']
})
export class RedirectCardComponent {
  @Input() bankName: string;
  @Output() cancelRedirect: EventEmitter<boolean> = new EventEmitter();
  @Output() proceedRedirect: EventEmitter<boolean> = new EventEmitter();

  constructor(public modal: NgbActiveModal) {}

  proceed(): void {
    this.proceedRedirect.emit(true);
  }

  cancel(): void {
    this.cancelRedirect.emit(true);
  }
}
