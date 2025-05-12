import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RouteBasedCardWithSidebarComponent } from './route-based-card-with-sidebar.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('RouteBasedCardWithSidebarComponent', () => {
  let component: RouteBasedCardWithSidebarComponent;
  let fixture: ComponentFixture<RouteBasedCardWithSidebarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RouteBasedCardWithSidebarComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
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
