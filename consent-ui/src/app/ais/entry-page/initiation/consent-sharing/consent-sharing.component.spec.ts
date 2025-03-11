import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ConsentSharingComponent } from './consent-sharing.component';
import { StubUtilTests } from '../../../common/stub-util-tests';
import { UpdateConsentAuthorizationService } from '../../../../api';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ConsentSharingComponent', () => {
  let component: ConsentSharingComponent;
  let consentAuthorizationService: UpdateConsentAuthorizationService;
  let fixture: ComponentFixture<ConsentSharingComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ConsentSharingComponent],
        imports: [],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              parent: {
                snapshot: { params: { authId: StubUtilTests.AUTH_ID } }
              },
              snapshot: {
                queryParams: {}
              }
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentSharingComponent);
    component = fixture.componentInstance;
    consentAuthorizationService = TestBed.inject(UpdateConsentAuthorizationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call denyUsingPOST', () => {
    const consentAuthorizationServiceSpy = spyOn(consentAuthorizationService, 'denyUsingPOST').and.returnValue(of());
    component.onDeny();
    expect(consentAuthorizationServiceSpy).toHaveBeenCalled();
  });

  it('should call onConfirm', () => {
    const urlSpy = spyOn(component, 'onConfirm');
    component.onConfirm();
    expect(urlSpy).toHaveBeenCalled();
  });
});
