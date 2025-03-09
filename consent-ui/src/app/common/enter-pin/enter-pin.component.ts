import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { StubUtil } from '../utils/stub-util';
import { UpdateConsentAuthorizationService } from '../../api';
import { SessionService } from '../session.service';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-enter-pin',
  templateUrl: './enter-pin.component.html',
  styleUrls: ['./enter-pin.component.scss']
})
export class EnterPinComponent implements OnInit {
  @Input() title: string;
  @Input() wrongPassword: boolean;
  @Input() authorizationSessionId: string;
  @Output() enteredPin = new EventEmitter<any>();

  pinForm: UntypedFormGroup;
  redirectCode: string;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private authService: UpdateConsentAuthorizationService,
    private sessionService: SessionService
  ) {}

  ngOnInit() {
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
    this.pinForm = this.formBuilder.group({
      pin: ['', Validators.required]
    });
  }

  onSubmit() {
    // since API call is the same for AIS and PIS, it is handled here instead of the parent
    this.authService
      .embeddedUsingPOST(
        this.authorizationSessionId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        this.redirectCode,
        { scaAuthenticationData: { PSU_PASSWORD: this.pinForm.get('pin').value } },
        'response'
      )
      .subscribe((res) => {
        this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
        this.enteredPin.emit(res);
      });
  }
}
