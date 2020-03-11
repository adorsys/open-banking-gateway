import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Consts} from "../../../../../technical-embedded-ui/tpp-ui/src/app/consts";
import {ActivatedRoute} from "@angular/router";
import {ConsentAuthorizationService} from "../../api";
import {Helpers} from "../../../../../technical-embedded-ui/tpp-ui/src/app/app.component";

@Component({
  selector: 'consent-app-password-input-page',
  templateUrl: './password-input-page.component.html',
  styleUrls: ['./password-input-page.component.scss']
})
export class PasswordInputPageComponent implements OnInit {
  passwordForm: FormGroup;
  private authorizationSessionId: string;
  private redirectCode: string;

  constructor(private consentAuthorizationService: ConsentAuthorizationService,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.passwordForm = this.formBuilder.group({
      pin: ['', Validators.required]
    });

    this.activatedRoute.queryParams.subscribe(
      params => {
        this.authorizationSessionId = params['authorizationSessionId'];
        this.redirectCode = params['redirectCode'];
      }
    );
  }

  submit(): void {
    this.consentAuthorizationService.embeddedUsingPOST(this.authorizationSessionId,
      Helpers.uuidv4(),
      Helpers.uuidv4(),
      this.redirectCode,
      {scaAuthenticationData: {PSU_PASSWORD: this.passwordForm.get('pin').value}})
      .subscribe(
        res => {
          // TODO: forward to the next page
        },
        error => {
          window.location.href = error.url;
        });
  }

}
