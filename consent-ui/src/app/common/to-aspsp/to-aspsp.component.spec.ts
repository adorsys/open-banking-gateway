import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ToAspspComponent } from './to-aspsp.component';

describe('ToAspspComponent', () => {
  let component: ToAspspComponent;
  let fixture: ComponentFixture<ToAspspComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ToAspspComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToAspspComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
