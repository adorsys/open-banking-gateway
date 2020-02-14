import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-redirect-card',
  templateUrl: './redirect-card.component.html',
  styleUrls: ['./redirect-card.component.scss']
})
export class RedirectCardComponent implements OnInit {
  @Output() cancelRedirect: EventEmitter<boolean> = new EventEmitter();
  @Output() proceedRedirect: EventEmitter<string> = new EventEmitter();

  bankId = 'a17cf87f-e1a2-433c-ac85-9be2ab6bdf65';

  constructor() {}

  ngOnInit() {}

  proceed(): void {
    this.proceedRedirect.emit(this.bankId);
  }

  cancel(): void {
    this.cancelRedirect.emit(true);
  }
}
