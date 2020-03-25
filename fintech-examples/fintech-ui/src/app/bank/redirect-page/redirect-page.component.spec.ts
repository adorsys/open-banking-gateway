import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectPageComponent } from './redirect-page.component';
import { RedirectCardComponent } from '../redirect-card/redirect-card.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('RedirectPageComponent', () => {
  let component: RedirectPageComponent;
  let fixture: ComponentFixture<RedirectPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [RedirectPageComponent, RedirectCardComponent]
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
