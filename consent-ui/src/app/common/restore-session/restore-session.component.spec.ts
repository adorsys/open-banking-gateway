import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RestoreSessionComponent } from './restore-session.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('RestoreSessionComponent', () => {
  let component: RestoreSessionComponent;
  let fixture: ComponentFixture<RestoreSessionComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [RestoreSessionComponent],
        imports: [],
        providers: [provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(RestoreSessionComponent);
    component = fixture.componentInstance;
    component.authId = '1-AUTH';
    component.aspspRedirectCode = '1-CODE';
    component.result = 'ok';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
