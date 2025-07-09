import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ReactiveFormsModule } from '@angular/forms';
import { RegisterComponent } from './register.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../../common/auth.service';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { expect } from '@jest/globals';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy;
  let authService: AuthService;
  const usernameInput = 'alex';
  const passwordInput = 'password';
  const notMachingPasswordInput = 'not matching password';
  let form;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [RegisterComponent],
        imports: [ReactiveFormsModule, RouterTestingModule],
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
    fixture = TestBed.createComponent(RegisterComponent);
    authService = fixture.debugElement.injector.get(AuthService);
    component = fixture.componentInstance;
    fixture.detectChanges();
    form = component.registerForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should call registerUser in AuthService', () => {
    authServiceSpy = jest.spyOn(authService, 'userRegister');

    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue('1234');
    component.onSubmit();
    fixture.detectChanges();
    expect(authServiceSpy).toHaveBeenCalledWith({ login: 'alex', password: '1234' });
  });
  it('should be true if the passwords are not same', () => {
    authServiceSpy = jest.spyOn(authService, 'userRegister');

    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue(passwordInput);
    form.controls.confirmPassword.setValue(notMachingPasswordInput);
    component.onSubmit();
    fixture.detectChanges();

    expect(component.registerForm.invalid).toBe(true);
  });
  it('should be false if password is not set', () => {
    authServiceSpy = jest.spyOn(authService, 'userRegister');

    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue('');
    form.controls.confirmPassword.setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.registerForm.invalid).toBe(true);
  });
  it('should be false if username is not set', () => {
    authServiceSpy = jest.spyOn(authService, 'userRegister');

    form.controls.login.setValue('');
    form.controls.password.setValue(passwordInput);
    form.controls.confirmPassword.setValue(passwordInput);
    component.onSubmit();
    fixture.detectChanges();

    expect(component.registerForm.invalid).toBe(true);
  });
});
