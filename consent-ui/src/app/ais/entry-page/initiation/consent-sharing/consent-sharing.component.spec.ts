import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ConsentSharingComponent } from './consent-sharing.component';
import { StubUtilTests } from '../../../common/stub-util-tests';
import { UpdateConsentAuthorizationService } from '../../../../api';

describe('ConsentSharingComponent', () => {
  let component: ConsentSharingComponent;
  let consentAuthorizationService: UpdateConsentAuthorizationService;
  let fixture: ComponentFixture<ConsentSharingComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConsentSharingComponent],
      imports: [HttpClientTestingModule],
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
        }
      ]
    }).compileComponents();
  }));

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
