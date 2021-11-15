import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestoreSessionComponent } from './restore-session.component';

describe('RestoreSessionComponent', () => {
  let component: RestoreSessionComponent;
  let fixture: ComponentFixture<RestoreSessionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestoreSessionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestoreSessionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
