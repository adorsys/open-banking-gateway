import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { ResultPageComponent } from './result-page.component';
import { StubUtilTests } from '../common/stub-util-tests';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ResultPageComponent', () => {
  let component: ResultPageComponent;
  let fixture: ComponentFixture<ResultPageComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ResultPageComponent],
        schemas: [NO_ERRORS_SCHEMA],
        imports: [RouterTestingModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                parent: { params: { authId: StubUtilTests.AUTH_ID } },
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
    fixture = TestBed.createComponent(ResultPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
