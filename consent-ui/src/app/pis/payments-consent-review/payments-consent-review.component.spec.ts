import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentsConsentReviewComponent } from './payments-consent-review.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { StubUtilTests } from '../../ais/common/stub-util-tests';

describe('PaymentsConsentReviewComponent', () => {
  let component: PaymentsConsentReviewComponent;
  let fixture: ComponentFixture<PaymentsConsentReviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentsConsentReviewComponent],
      imports: [RouterTestingModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { parent: { params: of(convertToParamMap({ authId: StubUtilTests.AUTH_ID })) } }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentsConsentReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
