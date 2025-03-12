import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { AuthStateConsentAuthorizationService, ScaUserData, UpdateConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { SessionService } from '../session.service';
import { StubUtil } from '../utils/stub-util';

@Component({
    selector: 'consent-app-select-sca',
    templateUrl: './select-sca.component.html',
    styleUrls: ['./select-sca.component.scss'],
    standalone: false
})
export class SelectScaComponent implements OnInit {
  @Input() authorizationSessionId: string;
  @Output() selectedValue = new EventEmitter<any>();

  scaMethods: ScaUserData[] = [];
  selectedMethod = new UntypedFormControl();
  scaMethodForm: UntypedFormGroup;
  redirectCode: string;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private authStateConsentAuthorizationService: AuthStateConsentAuthorizationService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService,
    private sessionService: SessionService
  ) {}

  ngOnInit() {
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);

    // init form
    this.scaMethodForm = this.formBuilder.group({
      selectedMethodValue: [this.selectedMethod, Validators.required]
    });
    this.initSca();
  }

  onSubmit(): void {
    this.updateConsentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationSessionId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stub
        this.redirectCode,
        { scaAuthenticationData: { SCA_CHALLENGE_ID: this.selectedMethod.value } },
        'response'
      )
      .subscribe((res) => {
        this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
        this.selectedValue.emit(res);
      });
  }

  private initSca(): void {
    this.authStateConsentAuthorizationService
      .authUsingGET(this.authorizationSessionId, this.redirectCode, 'response')
      .subscribe((consentAuth) => {
        this.sessionService.setRedirectCode(
          this.authorizationSessionId,
          consentAuth.headers.get(ApiHeaders.X_XSRF_TOKEN)
        );
        this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
        this.scaMethods = consentAuth.body.scaMethods;
        this.selectedMethod.setValue(this.scaMethods[0].id);
      });
  }
}
