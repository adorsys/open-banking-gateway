import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbeddedStartComponent } from './embedded-start.component';

describe('EmbeddedStartComponent', () => {
  let component: EmbeddedStartComponent;
  let fixture: ComponentFixture<EmbeddedStartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EmbeddedStartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbeddedStartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
