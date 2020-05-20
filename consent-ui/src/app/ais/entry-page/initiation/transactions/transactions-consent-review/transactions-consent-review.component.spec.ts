import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TransactionsConsentReviewComponent} from './transactions-consent-review.component';
import {RouterTestingModule} from '@angular/router/testing';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';
import {StubUtilTests} from '../../../../common/stub-util-tests';
import {SessionService} from "../../../../../common/session.service";
import {ConsentAuth, ConsentAuthorizationService, PsuAuthRequest} from "../../../../../api";
import {Location} from "@angular/common";
import {StubUtil} from "../../../../common/stub-util";
import {AisConsentToGrant} from "../../../../common/dto/ais-consent";
import {ConsentUtil} from "../../../../common/consent-util";

fdescribe('TransactionsConsentReviewComponent', () => {
    let component: TransactionsConsentReviewComponent;
    let fixture: ComponentFixture<TransactionsConsentReviewComponent>;
    let consentAuthorizationServiceSpy;
    let consentAuthorizationService: ConsentAuthorizationService;
    let route: ActivatedRoute;
    let authID ;
    let sessionService: SessionService;
    let aisConsent: AisConsentToGrant;
    let sessionServiceSpy;

    const locationStub = {
        back: jasmine.createSpy('onBack')
    };

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [TransactionsConsentReviewComponent],
            imports: [RouterTestingModule, ReactiveFormsModule, HttpClientTestingModule],
            providers: [
                SessionService,
                ConsentAuthorizationService,
                FormBuilder,
                {provide: Location, useValue: locationStub},
                {
                    provide: ActivatedRoute,
                    useValue: {parent: {parent: {params: of({authId: StubUtilTests.AUTH_ID})}}}
                }
            ]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(TransactionsConsentReviewComponent);
        component = fixture.componentInstance;
        route = TestBed.get(ActivatedRoute);
        sessionService = TestBed.get(SessionService);
        fixture.detectChanges();
        consentAuthorizationService = fixture.debugElement.injector.get(ConsentAuthorizationService);
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should go back after back button is pressed', () => {
        component.onBack();
        const location = fixture.debugElement.injector.get(Location);
        expect(location.back).toHaveBeenCalled();
    });

    fit('should confirm transanction when confirm button is pressed', () => {
        const xsrfToken = StubUtil.X_XSRF_TOKEN;
        const xrequestId = StubUtil.X_REQUEST_ID;
        const redirectCode = 'redirectCode654';
        const body = {extras: aisConsent.extras} as PsuAuthRequest;

        consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'embeddedUsingPOST').and.callThrough();
        sessionServiceSpy = spyOn(sessionService, 'getRedirectCode').and.callThrough();

        route.parent.parent.params.subscribe(resp => {
            authID = resp.authId;
            aisConsent = ConsentUtil.getOrDefault(authID, sessionService);
        });
        body.consentAuth = {consent: aisConsent.consent} as ConsentAuth;

        component.onConfirm();
        fixture.detectChanges();

        expect(sessionServiceSpy).toHaveBeenCalledWith(redirectCode);
        expect(consentAuthorizationServiceSpy).toHaveBeenCalledWith(authID, xsrfToken, xrequestId, redirectCode, body, 'response');
    })

});
