import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsentInitiateComponent } from './consent-initiate.component';

describe('ConsentInitiateComponent', () => {
  let component: ConsentInitiateComponent;
  let fixture: ComponentFixture<ConsentInitiateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConsentInitiateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentInitiateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
