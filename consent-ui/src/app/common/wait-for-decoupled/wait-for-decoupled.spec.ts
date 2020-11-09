import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WaitForDecoupled } from './wait-for-decoupled';

describe('ToAspspComponent', () => {
  let component: WaitForDecoupled;
  let fixture: ComponentFixture<WaitForDecoupled>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [WaitForDecoupled]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WaitForDecoupled);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
