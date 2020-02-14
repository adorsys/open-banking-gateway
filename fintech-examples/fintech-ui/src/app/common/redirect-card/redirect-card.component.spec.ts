import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectCardComponent } from './redirect-card.component';

describe('RedirectPageComponent', () => {
  let component: RedirectCardComponent;
  let fixture: ComponentFixture<RedirectCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RedirectCardComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
