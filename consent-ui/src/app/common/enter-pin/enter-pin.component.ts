import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StubUtil } from '../utils/stub-util';
import { ApiHeaders } from '../../api/api.headers';
import { ConsentAuthorizationService } from '../../api/api/consentAuthorization.service';
import { UpdateConsentAuthorizationService } from '../../api';
import { SessionService } from '../session.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'consent-app-enter-pin',
  templateUrl: './enter-pin.component.html',
  styleUrls: ['./enter-pin.component.scss']
})
export class EnterPinComponent implements OnInit {
  pinForm: FormGroup;
  @Input() wrongPassword: boolean;
  @Input() redirectCode: string;
  @Input() authorizationSessionId: string;
  @Output() enteredPin = new EventEmitter<any>();

  constructor(
    private formBuilder: FormBuilder,
    private authService: UpdateConsentAuthorizationService,
    private sessionService: SessionService
  ) {}

  ngOnInit() {
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
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        this.redirectCode,
        { scaAuthenticationData: { PSU_PASSWORD: this.pinForm.get('pin').value } },
        'response'
      )
      .subscribe(res => this.enteredPin.emit(res));
  }
}
