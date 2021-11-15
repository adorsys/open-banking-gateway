import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestoreSessionPageComponent } from './restore-session-page.component';

describe('RestoreSessionPageComponent', () => {
  let component: RestoreSessionPageComponent;
  let fixture: ComponentFixture<RestoreSessionPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestoreSessionPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestoreSessionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
