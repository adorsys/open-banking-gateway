import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PaymentInitiateComponent } from './payment-initiate.component';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('InitiationComponent', () => {
  let component: PaymentInitiateComponent;
  let fixture: ComponentFixture<PaymentInitiateComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [PaymentInitiateComponent],
        imports: [ReactiveFormsModule, RouterTestingModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                params: { authId: StubUtilTests.AUTH_ID },
                queryParams: { redirectCode: StubUtilTests.REDIRECT_ID }
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
    fixture = TestBed.createComponent(PaymentInitiateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
