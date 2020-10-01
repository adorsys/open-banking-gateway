import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { EnterPinPageComponent } from './enter-pin-page.component';
import { StubUtilTests } from '../common/stub-util-tests';

describe('AIS EnterPinPageComponent', () => {
  let component: EnterPinPageComponent;
  let fixture: ComponentFixture<EnterPinPageComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterPinPageComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [ReactiveFormsModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { queryParamMap: convertToParamMap({}) },
            parent: { snapshot: { paramMap: convertToParamMap({ authId: StubUtilTests.AUTH_ID }) } }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterPinPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
