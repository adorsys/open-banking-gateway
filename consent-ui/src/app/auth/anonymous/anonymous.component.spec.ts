import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AnonymousComponent } from './anonymous.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, Params } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../common/auth.service';

export class MockActivatedRoute {
  snapshot: ActivatedRouteSnapshot;
  param: Observable<Params>;
}

describe('AnonymousComponent', () => {
  let component: AnonymousComponent;
  let fixture: ComponentFixture<AnonymousComponent>;
  let route;
  let authServiceSpy;
  let authService: AuthService;
  const headersOpt = new HttpHeaders({ Location: 'httpw://localhost:9876/?id=77168991' });
  const usernameInput = 'alex';
  const passwordInput = '1234';

  beforeEach(async(() => {
    route = new MockActivatedRoute();
    route.snapshot = {
      queryParams: { redirectCode: 'redirectCode654' },
      parent: { params: { authId: 'authIdHere' } }
    };

    TestBed.configureTestingModule({
      declarations: [AnonymousComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnonymousComponent);
    authService = fixture.debugElement.injector.get(AuthService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call login service', () => {
    authServiceSpy = spyOn(authService, 'userLoginForAnonymousPayment').and.callThrough();

    const authID = route.snapshot.parent.params.authId;
    const redirectCode = 'redirectCode654';
    component.onSubmit();
    fixture.detectChanges();

    expect(authServiceSpy).toHaveBeenCalledWith(authID, redirectCode);
  });
});
