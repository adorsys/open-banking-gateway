import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {AuthService} from '../../common/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy;
  let authService: AuthService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RegisterComponent ],
      imports: [ ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    authService = fixture.debugElement.injector.get(AuthService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should call registerUser in AuthService', () => {
    authServiceSpy = spyOn(authService, 'userRegister').and.callThrough();
    const form = component.loginForm;
    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('1234');
    component.onSubmit();
    fixture.detectChanges();
    expect(authServiceSpy).toHaveBeenCalledWith({ login: 'alex', password: '1234' });
  });
  it('should be true if the passwords are not same', () => {
    authServiceSpy = spyOn(authService, 'userLogin').and.callThrough();
    const form = component.loginForm;
    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('adf');
    form.controls['confirmPassword'].setValue('asdf');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
  it('should be false if password is not set', () => {
    authServiceSpy = spyOn(authService, 'userLogin').and.callThrough();
    const form = component.loginForm;
    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('');
    form.controls['confirmPassword'].setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
  it('should be false if username is not set', () => {
    authServiceSpy = spyOn(authService, 'userLogin').and.callThrough();
    const form = component.loginForm;
    form.controls['login'].setValue('');
    form.controls['password'].setValue('asdf');
    form.controls['confirmPassword'].setValue('asdf');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });

});
