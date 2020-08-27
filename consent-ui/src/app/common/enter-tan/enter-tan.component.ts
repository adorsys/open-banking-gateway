import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { StubUtil } from '../utils/stub-util';
import { UpdateConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { SessionService } from '../session.service';
import { ConsentAuthorizationService } from '../../api/api/consentAuthorization.service';

@Component({
  selector: 'consent-app-enter-tan',
  templateUrl: './enter-tan.component.html',
  styleUrls: ['./enter-tan.component.scss']
})
export class EnterTanComponent implements OnInit {
  @Input() authorizationSessionId: string;
  @Input() scaType: string;
  @Input() wrongSca: boolean;
  @Output() enteredSca = new EventEmitter<any>();

  reportScaResultForm: FormGroup;
  redirectCode: string;
  baseImageUrl = 'data:image/jpg;base64,';

  public tanConfig = new TanConfig();
  public tanType = TanType;

  constructor(
    private formBuilder: FormBuilder,
    private sessionService: SessionService,
    private consentAuthorizationService: ConsentAuthorizationService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
    this.reportScaResultForm = this.formBuilder.group({
      tan: ['', Validators.required]
    });

    this.consentAuthorizationService
      .authUsingGET(this.authorizationSessionId, this.redirectCode, 'response')
      .subscribe(response => {
        let message = 'Please check your ' + this.scaType + ' and fill in the field below.';
        this.sessionService.setRedirectCode(
          this.authorizationSessionId,
          response.headers.get(ApiHeaders.REDIRECT_CODE)
        );

        const tmp: any = response.body;
        const challengeData = tmp.challengeData;

        if (challengeData.data[0] != null) {
          this.buildTanConfig(TanType.CHIP_OTP, challengeData.data[0], message);
        } else if (challengeData.image) {
          this.buildTanConfig(TanType.PHOTO_OTP, this.baseImageUrl + challengeData.image, message);
        } else {
          message = 'We have sent you the confirmation number. ' + message;
          this.buildTanConfig(TanType.PIN, '', message);
        }
      });
  }

  onSubmit(): void {
    this.updateConsentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationSessionId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        this.sessionService.getRedirectCode(this.authorizationSessionId),
        { scaAuthenticationData: { SCA_CHALLENGE_DATA: this.reportScaResultForm.get('tan').value } },
        'response'
      )
      .subscribe(res => {
        this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        this.enteredSca.emit(res);
      });
  }

  private buildTanConfig(type: TanType, data: string, message: string): void {
    this.tanConfig = { type, data, description: message };
  }
}

export class TanConfig {
  type?: TanType;
  data?: string;
  description?: string;
}

export enum TanType {
  PIN,
  QR_CODE,
  CHIP_OTP,
  PHOTO_OTP
}
