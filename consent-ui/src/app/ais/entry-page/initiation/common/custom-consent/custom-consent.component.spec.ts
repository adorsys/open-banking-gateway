import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomConsentComponent } from './custom-consent.component';
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {ActivatedRoute, convertToParamMap, Router} from "@angular/router";
import {of} from "rxjs";
import {StubUtilTests} from "../../../../common/stub-util-tests";

describe('CustomConsentComponent', () => {
  let component: CustomConsentComponent;
  let fixture: ComponentFixture<CustomConsentComponent>;

  const route = { navigate: jasmine.createSpy('navigate') };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CustomConsentComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [
        {
          provide: Router,
          useValue: route
        },
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { parent: { params: of({ authId: StubUtilTests.AUTH_ID }) } },
            snapshot: { queryParamMap: convertToParamMap({ wrong: false }) }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomConsentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
