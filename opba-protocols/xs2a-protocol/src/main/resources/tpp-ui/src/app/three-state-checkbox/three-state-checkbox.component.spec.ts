import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ThreeStateCheckboxComponent } from './three-state-checkbox.component';

describe('ThreeStateCheckboxComponent', () => {
  let component: ThreeStateCheckboxComponent;
  let fixture: ComponentFixture<ThreeStateCheckboxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ThreeStateCheckboxComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ThreeStateCheckboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
