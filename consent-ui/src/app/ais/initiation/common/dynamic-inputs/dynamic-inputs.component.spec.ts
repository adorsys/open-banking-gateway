import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicInputsComponent } from './dynamic-inputs.component';

describe('DynamicInputsComponent', () => {
  let component: DynamicInputsComponent;
  let fixture: ComponentFixture<DynamicInputsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DynamicInputsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DynamicInputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
