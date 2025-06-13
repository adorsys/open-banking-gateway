import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { SelectScaPageComponent } from './select-sca-page.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('PIS SelectScaPageComponent', () => {
  let component: SelectScaPageComponent;
  let fixture: ComponentFixture<SelectScaPageComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [SelectScaPageComponent],
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
    fixture = TestBed.createComponent(SelectScaPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
