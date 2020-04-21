import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import {AuthService} from '../../common/auth.service';
import {SessionService} from '../../common/session.service';
import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {Observable, of} from 'rxjs'
import {ActivatedRoute, ActivatedRouteSnapshot, Params} from '@angular/router';

export class MockActivatedRoute {
  snapshot: ActivatedRouteSnapshot;
  param: Observable<Params>
}

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let route;
  let authServiceSpy;
  let authService: AuthService;


  beforeEach(async(() => {
    route = new MockActivatedRoute();
    route.snapshot = {
      queryParams: of({redirectCode: '123456'}),
      parent: { params: of({authId: 'myid123459'}) }
    };

    TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      imports: [ ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule ],
      providers: [
        { provide: ActivatedRoute, useValue: route }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    authService = fixture.debugElement.injector.get(AuthService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should coll login service', () => {
    authServiceSpy = spyOn(authService, 'userLogin').and.callThrough();
    const form = component.loginForm;
    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('1234');
    component.onSubmit();
    fixture.detectChanges();

    expect(authServiceSpy).toHaveBeenCalledWith({ login: 'alex', password: '1234' });
  });
  it('should be true if the form is invalid', () => {
    authServiceSpy = spyOn(authService, 'userLogin').and.callThrough();
    const form = component.loginForm;
    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
  it('should be invalid if password is not set', () => {
    authServiceSpy = spyOn(authService, 'userLogin').and.callThrough();
    const form = component.loginForm;
    form.controls['login'].setValue('alex');
    form.controls['password'].setValue('');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
  it('should be invalid if username is not set', () => {
    authServiceSpy = spyOn(authService, 'userLogin').and.callThrough();
    const form = component.loginForm;
    form.controls['login'].setValue('');
    form.controls['password'].setValue('asdasf');
    component.onSubmit();
    fixture.detectChanges();

    expect(component.loginForm.invalid).toBe(true);
  });
});
