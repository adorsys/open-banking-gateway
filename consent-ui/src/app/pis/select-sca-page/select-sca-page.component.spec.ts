import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { SelectScaPageComponent } from './select-sca-page.component';
import { StubUtilTests } from '../../ais/common/stub-util-tests';

describe('PIS SelectScaPageComponent', () => {
  let component: SelectScaPageComponent;
  let fixture: ComponentFixture<SelectScaPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelectScaPageComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              snapshot: {
                paramMap: convertToParamMap({
                  authId: StubUtilTests.AUTH_ID
                })
              }
            }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
