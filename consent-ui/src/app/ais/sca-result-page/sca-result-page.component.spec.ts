import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportScaResultComponent } from './sca-result-page.component';

describe('ScaResultPageComponent', () => {
  let component: ReportScaResultComponent;
  let fixture: ComponentFixture<ReportScaResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReportScaResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportScaResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
