import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsentSharingComponent } from './consent-sharing.component';

describe('ConsentSharingComponent', () => {
  let component: ConsentSharingComponent;
  let fixture: ComponentFixture<ConsentSharingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConsentSharingComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentSharingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
