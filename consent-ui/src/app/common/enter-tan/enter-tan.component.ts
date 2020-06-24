import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StubUtil } from '../utils/stub-util';
import { ApiHeaders } from '../../api/api.headers';
import { UpdateConsentAuthorizationService } from '../../api';

@Component({
  selector: 'consent-app-enter-tan',
  templateUrl: './enter-tan.component.html',
  styleUrls: ['./enter-tan.component.scss']
})
export class EnterTanComponent implements OnInit {
  reportScaResultForm: FormGroup;
  @Input() authorizationSessionId: string;
  @Input() redirectCode: string;
  @Input() wrongSca: boolean;
  @Output() enteredSca = new EventEmitter<any>();

  constructor(
    private formBuilder: FormBuilder,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  ngOnInit() {
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
      .subscribe(res => this.enteredSca.emit(res));
  }
}
