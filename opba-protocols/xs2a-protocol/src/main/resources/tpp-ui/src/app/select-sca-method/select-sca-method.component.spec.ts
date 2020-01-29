import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectScaMethodComponent } from './select-sca-method.component';

describe('SelectScaMethodComponent', () => {
  let component: SelectScaMethodComponent;
  let fixture: ComponentFixture<SelectScaMethodComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelectScaMethodComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaMethodComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
