import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ParametersInputComponent } from './parameters-input.component';

describe('ParametersInputComponent', () => {
  let component: ParametersInputComponent;
  let fixture: ComponentFixture<ParametersInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ParametersInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParametersInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
