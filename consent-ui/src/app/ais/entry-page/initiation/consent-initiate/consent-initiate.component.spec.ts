import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ConsentInitiateComponent } from './consent-initiate.component';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { StubUtilTests } from '../../../common/stub-util-tests';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ConsentInitiateComponent', () => {
  let component: ConsentInitiateComponent;
  let fixture: ComponentFixture<ConsentInitiateComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ConsentInitiateComponent],
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
    fixture = TestBed.createComponent(ConsentInitiateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
