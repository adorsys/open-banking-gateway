import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardWithSidebarComponent } from './card-with-sidebar.component';

describe('SidebarComponent', () => {
  let component: CardWithSidebarComponent;
  let fixture: ComponentFixture<CardWithSidebarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardWithSidebarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardWithSidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
