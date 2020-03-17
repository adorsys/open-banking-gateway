import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ConsentAuthorizationService} from "../../api";
import {SessionService} from "../../common/session.service";
import {ApiHeaders} from "../../api/api.headers";
import {StubUtil} from "../common/stub-util";
import {Subscription} from "rxjs";

@Component({
    selector: 'consent-app-sca-select-page',
    templateUrl: './sca-select-page.component.html',
    styleUrls: ['./sca-select-page.component.scss']
})
export class ScaSelectPageComponent implements OnInit {

    authorizationSessionId: string = '';
    redirectCode: string = '';
    scaMethodForm: FormGroup;
    scaMethods: ScaMethod[];
    private subscriptions: Subscription[] = [];

    constructor(private sessionService: SessionService,
                private consentAuthorizationService: ConsentAuthorizationService,
                private route: ActivatedRoute,
                private formBuilder: FormBuilder) {
    }

    ngOnInit() {
        this.authorizationSessionId = this.route.parent.snapshot.paramMap.get("authId");
        this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
        this.loadAvailableMethods();
    }


    private loadAvailableMethods(): void {
        this.consentAuthorizationService.authUsingGET(this.authorizationSessionId, this.redirectCode, 'response')
            .subscribe(consentAuth => {
            /*     this.sessionService.setRedirectCode(this.authorizationSessionId, consentAuth.headers.get(ApiHeaders.REDIRECT_CODE));
                 this.scaMethods = consentAuth.body.consentAuth.scaMethods;
                this.initialScaMethodForm();*/
            });

        this.scaMethods = this.getScaMethodMock();
        this.initialScaMethodForm();
    }

    private initialScaMethodForm(): void {
        this.scaMethodForm = this.formBuilder.group({
            selectedMethodValue: [this.scaMethods[0].methodValue, Validators.required],
        });
    }

    private getScaMethodMock() {
        return [
            {
                id: "jbmvs-XUSXEnLI9KtatgIo",
                methodValue: "EMAIL:max.musterman@mail.de"
            },
            {
                id: "Hqn8MrHUREIjGNYpGq38Jg",
                methodValue: "EMAIL:max.musterman2@mail.de"
            }
        ];
    }

    onSubmit(): void {
        this.subscriptions.push(
            this.consentAuthorizationService.embeddedUsingPOST(
                this.authorizationSessionId,
                StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
                StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
                this.redirectCode,
                {scaAuthenticationData: {SCA_CHALLENGE_ID: this.scaMethodForm.get('selectedMethodValue').value}},
                "response"
            ).subscribe(
                res => {
                    // redirect to the provided location
                    console.log("REDIRECTING TO: " + res.headers.get(ApiHeaders.LOCATION));
                    this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
                    window.location.href = res.headers.get(ApiHeaders.LOCATION);
                },
                error => {
                    console.log(error);
                    // window.location.href = error.url;
                })
        );
    }
}

class ScaMethod {
    id: string;
    methodValue: string;
}
