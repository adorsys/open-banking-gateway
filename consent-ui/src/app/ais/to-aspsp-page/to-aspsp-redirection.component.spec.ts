import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { of } from 'rxjs';
import { StubUtilTests } from '../common/stub-util-tests';
import { ToAspspRedirectionComponent } from './to-aspsp-redirection.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ToAspspRedirectionComponent', () => {
  let component: ToAspspRedirectionComponent;
  let fixture: ComponentFixture<ToAspspRedirectionComponent>;

  beforeAll(() => (window.onbeforeunload = jest.fn()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ToAspspRedirectionComponent],
        schemas: [NO_ERRORS_SCHEMA],
        imports: [RouterTestingModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              parent: {
                params: of({ authId: StubUtilTests.AUTH_ID }),
                queryParams: of({ redirectCode: StubUtilTests.REDIRECT_ID })
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
    fixture = TestBed.createComponent(ToAspspRedirectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
