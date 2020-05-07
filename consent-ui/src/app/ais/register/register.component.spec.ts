import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReactiveFormsModule } from '@angular/forms';
import { RegisterComponent } from './register.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { StubUtilTests } from '../common/stub-util-tests';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {AuthService} from '../../common/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy;
  let authService: AuthService;
  const usernameInput = 'alex';
  const passwordInput  = 'password';
  const notMachingPasswordInput = 'not matching password';
  let form;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              parent: { params: { authId: StubUtilTests.AUTH_ID } },
              queryParams: { redirectCode: StubUtilTests.REDIRECT_ID }
            }
          }
        }
      ]
    }).compileComponents();
  }));

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
    authServiceSpy = spyOn(authService, 'userRegister').and.callThrough();

    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue('1234');
    component.onSubmit();
    fixture.detectChanges();
    expect(authServiceSpy).toHaveBeenCalledWith({ login: 'alex', password: '1234' });
  });
  it('should be true if the passwords are not same', () => {
    authServiceSpy = spyOn(authService, 'userRegister').and.callThrough();

    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue(passwordInput);
    form.controls.confirmPassword.setValue(notMachingPasswordInput);
    component.onSubmit();
    fixture.detectChanges();

    expect(component.registerForm.invalid).toBe(true);
  });
  it('should be false if password is not set', () => {
    authServiceSpy = spyOn(authService, 'userRegister').and.callThrough();

    form.controls.login.setValue(usernameInput);
    form.controls.password.setValue('');
    form.controls.confirmPassword.setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.registerForm.invalid).toBe(true);
  });
  it('should be false if username is not set', () => {
    authServiceSpy = spyOn(authService, 'userRegister').and.callThrough();

    form.controls.login.setValue('');
    form.controls.password.setValue(passwordInput);
    form.controls.confirmPassword.setValue(passwordInput);
    component.onSubmit();
    fixture.detectChanges();

    expect(component.registerForm.invalid).toBe(true);
  });
});
