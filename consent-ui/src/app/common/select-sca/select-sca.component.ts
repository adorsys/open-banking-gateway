import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthStateConsentAuthorizationService, ScaUserData, UpdateConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { SessionService } from '../session.service';
import { StubUtil } from '../utils/stub-util';

@Component({
  selector: 'consent-app-select-sca',
  templateUrl: './select-sca.component.html',
  styleUrls: ['./select-sca.component.scss']
})
export class SelectScaComponent implements OnInit {
  @Input() authorizationSessionId: string;
  @Input() redirectCode: string;
  @Output() selectedValue = new EventEmitter<any>();
  scaMethods: ScaUserData[] = [];
  selectedMethod = new FormControl();
  scaMethodForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private authStateConsentAuthorizationService: AuthStateConsentAuthorizationService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService,
    private sessionService: SessionService
  ) {}

  ngOnInit() {
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
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        this.redirectCode,
        { scaAuthenticationData: { SCA_CHALLENGE_ID: this.selectedMethod.value } },
        'response'
      )
      .subscribe(res => this.selectedValue.emit(res));
  }

  private initSca(): void {
    this.authStateConsentAuthorizationService
      .authUsingGET(this.authorizationSessionId, this.redirectCode, 'response')
      .subscribe(consentAuth => {
        this.sessionService.setRedirectCode(
          this.authorizationSessionId,
          consentAuth.headers.get(ApiHeaders.REDIRECT_CODE)
        );
        this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
        this.scaMethods = consentAuth.body.consentAuth.scaMethods;
        this.selectedMethod.setValue(this.scaMethods[0].id);
      });
  }
}
