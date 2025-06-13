import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SessionService } from '../session.service';

@Component({
  selector: 'consent-app-to-aspsp',
  templateUrl: './to-aspsp.component.html',
  styleUrls: ['./to-aspsp.component.scss'],
  standalone: false
})
export class ToAspspComponent implements OnInit {
  @Input() redirectTo: string;
  @Input() aspspName: string;
  @Input() actionName: string;
  @Input() finTechName: string;
  @Input() authorizationSessionId: string;
  @Output() deny = new EventEmitter<boolean>();

  constructor(private sessionService: SessionService) {}

  ngOnInit() {}

  onConfirm() {
    this.sessionService.setIsLongTimeCookie(this.authorizationSessionId, true);
    window.location.href = this.redirectTo;
  }

  onDeny(): void {
    this.deny.emit();
  }
}
