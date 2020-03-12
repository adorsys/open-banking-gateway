import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ToAspspRedirectionComponent } from './to-aspsp-redirection.component';

describe('ToAspspRedirectionComponent', () => {
  let component: ToAspspRedirectionComponent;
  let fixture: ComponentFixture<ToAspspRedirectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ToAspspRedirectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToAspspRedirectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
