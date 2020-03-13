import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteBasedCardWithSidebarComponent } from './route-based-card-with-sidebar.component';

describe('SidebarComponent', () => {
  let component: RouteBasedCardWithSidebarComponent;
  let fixture: ComponentFixture<RouteBasedCardWithSidebarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RouteBasedCardWithSidebarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RouteBasedCardWithSidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
