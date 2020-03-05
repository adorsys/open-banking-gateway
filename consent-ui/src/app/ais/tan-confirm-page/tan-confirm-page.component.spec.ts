import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TanConfirmPageComponent } from './tan-confirm-page.component';

describe('TanConfirmPageComponent', () => {
  let component: TanConfirmPageComponent;
  let fixture: ComponentFixture<TanConfirmPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TanConfirmPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TanConfirmPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
