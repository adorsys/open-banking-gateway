import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ToAspspRedirectionComponent } from './to-aspsp-redirection.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { StubUtilTests } from '../common/stub-util-tests';

describe('ToAspspRedirectionComponent', () => {
  let component: ToAspspRedirectionComponent;
  let fixture: ComponentFixture<ToAspspRedirectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ToAspspRedirectionComponent],
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { params: of({ authId: StubUtilTests.AUTH_ID }) }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToAspspRedirectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
