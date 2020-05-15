import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectScaPageComponent } from './select-sca-page.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('SelectScaPageComponent', () => {
  let component: SelectScaPageComponent;
  let fixture: ComponentFixture<SelectScaPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelectScaPageComponent],
      schemas: [NO_ERRORS_SCHEMA]
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
