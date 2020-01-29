import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportScaResultComponent } from './report-sca-result.component';

describe('ReportScaResultComponent', () => {
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
