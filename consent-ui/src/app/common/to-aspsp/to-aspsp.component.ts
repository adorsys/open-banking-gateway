import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'consent-app-to-aspsp',
  templateUrl: './to-aspsp.component.html',
  styleUrls: ['./to-aspsp.component.scss']
})
export class ToAspspComponent implements OnInit {
  @Input() redirectTo: string;
  @Input() aspspName: string;
  @Input() actionName: string;
  @Input() finTechName: string;
  @Output() deny = new EventEmitter<boolean>();

  constructor() {}

  ngOnInit() {}

  onConfirm() {
    window.location.href = this.redirectTo;
  }

  onDeny(): void {
    this.deny.emit();
  }
}
