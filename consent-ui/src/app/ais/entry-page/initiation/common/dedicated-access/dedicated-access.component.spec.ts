import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Location } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of } from 'rxjs';
import { expect } from '@jest/globals';

import { StubUtilTests } from '../../../../common/stub-util-tests';
import { SessionService } from '../../../../../common/session.service';
import { ConsentUtil } from '../../../../common/consent-util';
import { DedicatedAccessComponent } from './dedicated-access.component';

describe('DedicatedAccessComponent', () => {
  let component: DedicatedAccessComponent;
  let fixture: ComponentFixture<DedicatedAccessComponent>;
  let sessionService: SessionService;
  let location: Location;
  let sessionServiceSpy;
  let activatedRoute: ActivatedRoute;
  const route = { navigate: jest.fn().mockName('navigate') };
  let authId;
  let consentUtilSpy;
  let mockData;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [DedicatedAccessComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [ReactiveFormsModule, RouterTestingModule],
        providers: [
          {
            provide: Router,
            useValue: route
          },
          {
            provide: ActivatedRoute,
            useValue: {
              parent: { parent: { params: of({ authId: StubUtilTests.AUTH_ID }) } },
              snapshot: { queryParamMap: convertToParamMap({ wrong: false }) }
            }
          }
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DedicatedAccessComponent);
    sessionService = fixture.debugElement.injector.get(SessionService);
    component = fixture.componentInstance;
    activatedRoute = TestBed.inject(ActivatedRoute);
    location = TestBed.inject(Location);

    activatedRoute.parent.parent.params.subscribe((id) => (authId = id.authId));
    mockData = {
      level: null,
      consent: {
        access: {
          availableAccounts: null,
          allPsd2: null,
          accounts: null,
          balances: null,
          transactions: null
        },
        frequencyPerDay: 24,
        recurringIndicator: true,
        validUntil: '2099-06-24'
      }
    };
    consentUtilSpy = jest.spyOn(ConsentUtil, 'getOrDefault').mockReturnValue(mockData);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should test onBack method', () => {
    sessionServiceSpy = jest.spyOn(sessionService, 'setConsentObject');
    const locationSpy = jest.spyOn(location, 'back');
    component.onBack();

    expect(sessionServiceSpy).toHaveBeenCalledWith(authId, mockData);
    expect(consentUtilSpy).toHaveBeenCalled();
    expect(locationSpy).toHaveBeenCalled();
  });

  it('should test onSelect method', () => {
    sessionServiceSpy = jest.spyOn(sessionService, 'setConsentObject');
    component.onSelect();

    expect(sessionServiceSpy).toHaveBeenCalled();
    expect(consentUtilSpy).toHaveBeenCalled();
  });
});
