import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StubUtil } from '../utils/stub-util';
import { UpdateConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { SessionService } from '../session.service';

@Component({
  selector: 'consent-app-enter-tan',
  templateUrl: './enter-tan.component.html',
  styleUrls: ['./enter-tan.component.scss']
})
export class EnterTanComponent implements OnInit {
  @Input() authorizationSessionId: string;
  @Input() wrongSca: boolean;
  @Output() enteredSca = new EventEmitter<any>();

  reportScaResultForm: FormGroup;
  redirectCode: string;

  constructor(
    private formBuilder: FormBuilder,
    private sessionService: SessionService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
    this.reportScaResultForm = this.formBuilder.group({
      tan: ['', Validators.required]
    });
  }

  onSubmit(): void {
    this.updateConsentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationSessionId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        this.redirectCode,
        { scaAuthenticationData: { SCA_CHALLENGE_DATA: this.reportScaResultForm.get('tan').value } },
        'response'
      )
      .subscribe(res => {
        this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        this.enteredSca.emit(res);
      });
  }
}
