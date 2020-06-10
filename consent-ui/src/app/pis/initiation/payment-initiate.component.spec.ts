import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentInitiateComponent } from './payment-initiate.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { StubUtilTests } from '../../ais/common/stub-util-tests';

describe('InitiationComponent', () => {
  let component: PaymentInitiateComponent;
  let fixture: ComponentFixture<PaymentInitiateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentInitiateComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: { authId: StubUtilTests.AUTH_ID },
              queryParams: { redirectCode: StubUtilTests.REDIRECT_ID }
            }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentInitiateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
