import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LimitedAccessComponent } from './limited-access.component';

describe('LimitedAccessComponent', () => {
  let component: LimitedAccessComponent;
  let fixture: ComponentFixture<LimitedAccessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LimitedAccessComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LimitedAccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
