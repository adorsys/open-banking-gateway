import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { StubUtilTests } from '../common/stub-util-tests';
import { ScaSelectPageComponent } from './sca-select-page.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('AIS ScaSelectPageComponent', () => {
  let component: ScaSelectPageComponent;
  let fixture: ComponentFixture<ScaSelectPageComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ScaSelectPageComponent],
        schemas: [NO_ERRORS_SCHEMA],
        imports: [RouterTestingModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              parent: {
                snapshot: {
                  paramMap: convertToParamMap({
                    authId: StubUtilTests.AUTH_ID
                  })
                }
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
    fixture = TestBed.createComponent(ScaSelectPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
