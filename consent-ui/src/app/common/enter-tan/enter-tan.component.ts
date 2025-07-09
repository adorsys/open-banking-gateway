import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';

import { StubUtil } from '../utils/stub-util';
import { ChallengeData, UpdateConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { SessionService } from '../session.service';
import { AuthStateConsentAuthorizationService } from '../../api';
import { HttpResponse } from '@angular/common/http';

interface AuthStateResponse {
  challengeData: ChallengeData;
}

@Component({
  selector: 'consent-app-enter-tan',
  templateUrl: './enter-tan.component.html',
  styleUrls: ['./enter-tan.component.scss'],
  standalone: false
})
export class EnterTanComponent implements OnInit {
  @Input() authorizationSessionId: string;
  @Input() scaType: ScaType;
  @Input() wrongSca: boolean;
  @Output() enteredSca = new EventEmitter<HttpResponse<unknown>>();

  reportScaResultForm: UntypedFormGroup;
  redirectCode: string;
  baseImageUrl = 'data:image/jpg;base64,';
  private message = '';

  public tanConfig = new TanConfig();
  public tanType = ScaType;
  private challengeData: ChallengeData;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private sessionService: SessionService,
    private consentAuthorizationService: AuthStateConsentAuthorizationService,
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
      .subscribe((response) => {
        this.sessionService.setRedirectCode(this.authorizationSessionId, response.headers.get(ApiHeaders.X_XSRF_TOKEN));

        const authStateResponseBody = response.body as AuthStateResponse;
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
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stub
        this.sessionService.getRedirectCode(this.authorizationSessionId),
        { scaAuthenticationData: { SCA_CHALLENGE_DATA: this.reportScaResultForm.get('tan').value } },
        'response'
      )
      .subscribe((res) => {
        this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
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
