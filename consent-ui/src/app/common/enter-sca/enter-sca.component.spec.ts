import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EnterScaComponent } from './enter-sca.component';

describe('ScaSelectLayoutComponent', () => {
  let component: EnterScaComponent;
  let fixture: ComponentFixture<EnterScaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EnterScaComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterScaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
