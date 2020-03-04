import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswordInputPageComponent } from './password-input-page.component';

describe('PasswordInputPageComponent', () => {
  let component: PasswordInputPageComponent;
  let fixture: ComponentFixture<PasswordInputPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PasswordInputPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PasswordInputPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
