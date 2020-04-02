import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InitialRequestComponent } from './initial-request.component';

describe('InitialRequestComponent', () => {
  let component: InitialRequestComponent;
  let fixture: ComponentFixture<InitialRequestComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InitialRequestComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InitialRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
