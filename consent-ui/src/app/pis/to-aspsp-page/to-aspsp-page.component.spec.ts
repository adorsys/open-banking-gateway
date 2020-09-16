import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { ToAspspPageComponent } from './to-aspsp-page.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { StubUtilTests } from '../../ais/common/stub-util-tests';

describe('ToAspspPageComponent', () => {
  let component: ToAspspPageComponent;
  let fixture: ComponentFixture<ToAspspPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ToAspspPageComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              params: of({ authId: StubUtilTests.AUTH_ID }),
              queryParams: of({ redirectCode: StubUtilTests.REDIRECT_ID }),
            },
          },
        },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToAspspPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
