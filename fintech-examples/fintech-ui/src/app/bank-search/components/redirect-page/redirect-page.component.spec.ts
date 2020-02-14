import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectPageComponent } from './redirect-page.component';

describe('RedirectPageComponent', () => {
  let component: RedirectPageComponent;
  let fixture: ComponentFixture<RedirectPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RedirectPageComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
