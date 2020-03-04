import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PageConfirmPageComponent } from './page-confirm-page.component';

describe('PageConfirmPageComponent', () => {
  let component: PageConfirmPageComponent;
  let fixture: ComponentFixture<PageConfirmPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PageConfirmPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PageConfirmPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
