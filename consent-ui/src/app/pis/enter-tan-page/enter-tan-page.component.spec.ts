import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { EnterTanPageComponent } from './enter-tan-page.component';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { StubUtilTests } from '../../ais/common/stub-util-tests';
import { expect } from '@jest/globals';

describe('PIS EnterTanPageComponent', () => {
  let component: EnterTanPageComponent;
  let fixture: ComponentFixture<EnterTanPageComponent>;

  beforeAll(() => (window.onbeforeunload = jest.fn()));

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [EnterTanPageComponent],
        schemas: [NO_ERRORS_SCHEMA],
        imports: [ReactiveFormsModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: { paramMap: convertToParamMap({ scaType: 'scaType' }), queryParamMap: convertToParamMap({}) },
              parent: { snapshot: { paramMap: convertToParamMap({ authId: StubUtilTests.AUTH_ID }) } }
            }
          }
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterTanPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
