import {
  async,
  ComponentFixture,
  TestBed
} from '@angular/core/testing';

import {ConsentSharingComponent} from './consent-sharing.component';
import {ActivatedRoute} from '@angular/router';
import {StubUtilTests} from '../../../common/stub-util-tests';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from "rxjs";
import {ConsentAuthorizationService} from "../../../../api";

fdescribe('ConsentSharingComponent', () => {
  let component: ConsentSharingComponent;
  let consentAuthorizationService: ConsentAuthorizationService;
  let fixture: ComponentFixture<ConsentSharingComponent>;
  const MockWindow = {
    location: {
      _href: '',
      set href(url: string) { this._href = url },
      get href() { return this._href }
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConsentSharingComponent],
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              snapshot: {params: {authId: StubUtilTests.AUTH_ID}}
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
    consentAuthorizationService = fixture.debugElement.injector.get(ConsentAuthorizationService);
    fixture.detectChanges();
    const setHrefSpy = spyOnProperty(MockWindow.location, 'href', 'set');
    const getHrefSpy = spyOnProperty(MockWindow.location, 'href', 'get');
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

  })
});
