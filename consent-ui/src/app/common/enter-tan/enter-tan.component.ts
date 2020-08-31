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
  @Input() scaType: ScaType;
  @Input() wrongSca: boolean;
  @Output() enteredSca = new EventEmitter<any>();

  reportScaResultForm: FormGroup;
  redirectCode: string;
  baseImageUrl = 'data:image/jpg;base64,';
  private message = '';

  public tanConfig = new TanConfig();
  public tanType = ScaType;
  private challengeData: any;

  constructor(
    private formBuilder: FormBuilder,
    private sessionService: SessionService,
    private consentAuthorizationService: ConsentAuthorizationService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.message = 'Please check your ' + this.scaType + ' and fill in the field below.';
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
    this.reportScaResultForm = this.formBuilder.group({
      tan: ['', Validators.required]
    });

    this.consentAuthorizationService
      .authUsingGET(this.authorizationSessionId, this.redirectCode, 'response')
      .subscribe(response => {
        this.sessionService.setRedirectCode(
          this.authorizationSessionId,
          response.headers.get(ApiHeaders.REDIRECT_CODE)
        );

        const authStateResponseBody: any = response.body;
        this.challengeData = authStateResponseBody.challengeData;

        switch (this.scaType) {
          case ScaType.CHIP_OTP:
            this.buildChipOtp();
            break;
          case ScaType.PHOTO_OTP:
            this.buildPhotoOtp();
            break;
          default:
            this.buildEmailOrSmsOtp();
            break;
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

  private buildPhotoOtp(): void {
    this.tanConfig = {
      type: ScaType.PHOTO_OTP,
      data: this.baseImageUrl + this.challengeData.image,
      description: this.message
    };
  }

  private buildChipOtp(): void {
    this.tanConfig = { type: ScaType.CHIP_OTP, data: this.challengeData.data[0], description: this.message };
  }

  private buildEmailOrSmsOtp(): void {
    this.message = 'We have sent you the confirmation number. ' + this.message;
    this.tanConfig = { type: ScaType.EMAIL, data: '', description: this.message };
  }
}

export class TanConfig {
  type?: ScaType;
  data?: string;
  description?: string;
}

export enum ScaType {
  EMAIL = 'EMAIL',
  QR_CODE = 'QR_CODE',
  CHIP_OTP = 'CHIP_OTP',
  PHOTO_OTP = 'PHOTO_OTP'
}
