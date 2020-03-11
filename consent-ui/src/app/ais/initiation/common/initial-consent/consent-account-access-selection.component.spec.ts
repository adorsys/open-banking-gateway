import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsentAccountAccessSelectionComponent } from './consent-account-access-selection.component';

describe('EntryPageAccountsComponent', () => {
  let component: ConsentAccountAccessSelectionComponent;
  let fixture: ComponentFixture<ConsentAccountAccessSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConsentAccountAccessSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentAccountAccessSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
