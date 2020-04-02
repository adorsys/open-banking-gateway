import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FromAspspComponent } from './from-aspsp.component';

describe('FromAspspComponent', () => {
  let component: FromAspspComponent;
  let fixture: ComponentFixture<FromAspspComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FromAspspComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FromAspspComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
