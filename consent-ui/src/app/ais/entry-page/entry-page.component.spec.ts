import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { EntryPageComponent } from './entry-page.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { CookieRenewalService } from '../common/cookie-renewal/CookieRenewalService';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('EntryPageComponent', () => {
  let component: EntryPageComponent;
  let fixture: ComponentFixture<EntryPageComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [EntryPageComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [ReactiveFormsModule],
        providers: [
          CookieRenewalService,
          {
            provide: ActivatedRoute,
            useValue: { paramMap: of(convertToParamMap({ authId: '1234' })) }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EntryPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
