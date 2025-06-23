import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmComponent } from './confirm.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { ConfirmData } from './confirm.data';
import { RedirectStruct } from '../../redirect-page/redirect-struct';
import { ClassSinglePaymentInitiationRequest } from '../../../api/model-classes/ClassSinglePaymentInitiationRequest';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { of } from 'rxjs';

describe('ConfirmComponent', () => {
  let component: ConfirmComponent;
  let fixture: ComponentFixture<ConfirmComponent>;
  let mockConfirmData: ConfirmData;

  function setupMockData(): ConfirmData {
    const redirectStruct = new RedirectStruct();
    redirectStruct.bankName = 'peter';
    redirectStruct.redirectUrl = 'redirectUrl';

    const paymentRequest = new ClassSinglePaymentInitiationRequest();
    paymentRequest.debitorIban = 'DE80760700240271232400';
    paymentRequest.creditorIban = 'AL90208110080000001039531801';
    paymentRequest.amount = '1.10';
    paymentRequest.name = 'peter';

    const confirmData = new ConfirmData();
    confirmData.redirectStruct = redirectStruct;
    confirmData.paymentRequest = paymentRequest;

    return confirmData;
  }

  beforeAll(() => {
    window.onbeforeunload = jasmine.createSpy();
    mockConfirmData = setupMockData();
  });

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ConfirmComponent],
        providers: [
          provideRouter([]),
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting(),
          {
            provide: ActivatedRoute,
            useValue: {
              paramMap: {
                subscribe: jasmine.createSpy().and.callFake(() => {
                  return of(JSON.stringify(mockConfirmData));
                })
              }
            }
          }
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(ConfirmComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    })
  );

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call onDeny', () => {
    const onDenySpy = spyOn(component, 'onDeny');
    component.onDeny();
    expect(onDenySpy).toHaveBeenCalled();
  });

  it('should call onConfirm', () => {
    const onConfirmSpy = spyOn(component, 'onConfirm');
    component.onConfirm();
    expect(onConfirmSpy).toHaveBeenCalled();
  });

  it('amount of money to be wired should be rounded properly', () => {
    const numb = 2122;
    const rest = 2122.0;
    spyOn(component, 'roundToTwoDigitsAfterComma').withArgs(numb).and.callThrough();
    expect(numb).toEqual(rest);
  });
});
