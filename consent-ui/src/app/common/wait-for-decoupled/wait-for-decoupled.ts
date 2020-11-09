import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SessionService } from '../session.service';

@Component({
  selector: 'wait-for-decoupled',
  templateUrl: './wait-for-decoupled.html',
  styleUrls: ['./wait-for-decoupled.scss']
})
export class WaitForDecoupled implements OnInit {
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
