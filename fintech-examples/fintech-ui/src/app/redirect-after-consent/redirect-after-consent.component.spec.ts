import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectAfterConsentComponent } from './redirect-after-consent.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ConsentAuthorizationService } from '../bank/services/consent-authorization.service';

describe('RedirectAfterConsentComponent', () => {
  let component: RedirectAfterConsentComponent;
  let fixture: ComponentFixture<RedirectAfterConsentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RedirectAfterConsentComponent],
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        ConsentAuthorizationService,
        { provide: ActivatedRoute, useValue: { snapshot: { queryParams: { redirectCode: '123' } } } }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectAfterConsentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
