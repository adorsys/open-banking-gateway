import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FintechCallbackOkComponent } from './fintech-callback-ok.component';

describe('FintechCallbackOkComponent', () => {
  let component: FintechCallbackOkComponent;
  let fixture: ComponentFixture<FintechCallbackOkComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FintechCallbackOkComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FintechCallbackOkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
