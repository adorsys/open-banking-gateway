import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ConsentInfoComponent } from './consent-info.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('ConsentInfoComponent', () => {
  let component: ConsentInfoComponent;
  let fixture: ComponentFixture<ConsentInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConsentInfoComponent],
      imports: [RouterTestingModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
