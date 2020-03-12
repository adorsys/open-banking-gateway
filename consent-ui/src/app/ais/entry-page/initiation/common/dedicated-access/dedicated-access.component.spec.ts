import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DedicatedAccessComponent } from './dedicated-access.component';

describe('LimitedAccessComponent', () => {
  let component: DedicatedAccessComponent;
  let fixture: ComponentFixture<DedicatedAccessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DedicatedAccessComponent ]
    })
    .compileComponents();
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
