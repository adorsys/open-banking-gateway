import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportScaResultComponent } from './sca-result-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

describe('ReportScaResultComponent', () => {
  let component: ReportScaResultComponent;
  let fixture: ComponentFixture<ReportScaResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ReportScaResultComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { parent: { snapshot: { paramMap: convertToParamMap({ authId: 'AUTH-ID' }) } } }
        }
      ]
    }).compileComponents();
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
