import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScaSelectPageComponent } from './sca-select-page.component';

describe('ScaSelectPageComponent', () => {
  let component: ScaSelectPageComponent;
  let fixture: ComponentFixture<ScaSelectPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScaSelectPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScaSelectPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
