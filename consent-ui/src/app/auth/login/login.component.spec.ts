import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './login.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, Params } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AuthService } from '../../common/auth.service';
import { expect } from '@jest/globals';

export class MockActivatedRoute {
  snapshot: ActivatedRouteSnapshot;
  param: Observable<Params>;
}

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let route;
  let authServiceSpy;
  let authService: AuthService;
  const headersOpt = new HttpHeaders({ Location: 'httpw://localhost:9876/?id=77168991' });
  const response = new HttpResponse({
    body: { xsrfToken: 'tokenHere' },
    headers: headersOpt,
    status: 200,
    statusText: 'geht'
  });
  let form;
  const usernameInput = 'alex';
  const passwordInput = '1234';

  beforeAll(() => (window.onbeforeunload = jest.fn()));

  beforeEach(
    waitForAsync(() => {
      route = new MockActivatedRoute();
      route.snapshot = {
        queryParams: { redirectCode: 'redirectCode654' },
        parent: { params: { authId: 'authIdHere' } }
      };

      TestBed.configureTestingModule({
        declarations: [LoginComponent],
        imports: [ReactiveFormsModule, RouterTestingModule],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    authService = fixture.debugElement.injector.get(AuthService);
    component = fixture.componentInstance;
    fixture.detectChanges();
    form = component.loginForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be true if the form is invalid', () => {
    authServiceSpy = jest.spyOn(authService, 'userLogin').mockReturnValue(of(response));

    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });

  it('should call login service', () => {
    authServiceSpy = jest.spyOn(authService, 'userLogin');

    const authID = route.snapshot.parent.params.authId;
    const redirectCode = 'redirectCode654';
    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue(passwordInput);
    component.onSubmit();
    fixture.detectChanges();

    expect(authServiceSpy).toHaveBeenCalledWith(authID, redirectCode, {
      login: usernameInput,
      password: passwordInput
    });
  });

  it('should be invalid if password is not set', () => {
    authServiceSpy = jest.spyOn(authService, 'userLogin').mockReturnValue(of(response));

    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
  it('should be invalid if username is not set', () => {
    authServiceSpy = jest.spyOn(authService, 'userLogin').mockReturnValue(of(response));

    form.controls.login.setValue('');
    form.controls.password.setValue(passwordInput);
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
});
