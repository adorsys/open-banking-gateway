import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProvidePsuPasswordComponent } from './provide-psu-password.component';

describe('ProvidePsuPasswordComponent', () => {
  let component: ProvidePsuPasswordComponent;
  let fixture: ComponentFixture<ProvidePsuPasswordComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProvidePsuPasswordComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProvidePsuPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
