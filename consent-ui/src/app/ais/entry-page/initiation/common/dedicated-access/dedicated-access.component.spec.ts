import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DedicatedAccessComponent } from './dedicated-access.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { StubUtilTests } from '../../../../common/stub-util-tests';

describe('DedicatedAccessComponent', () => {
  let component: DedicatedAccessComponent;
  let fixture: ComponentFixture<DedicatedAccessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DedicatedAccessComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { parent: { params: of(convertToParamMap({ authId: StubUtilTests.AUTH_ID })) } },
            snapshot: { queryParamMap: convertToParamMap({}) }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DedicatedAccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
