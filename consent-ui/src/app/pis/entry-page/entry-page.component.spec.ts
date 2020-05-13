import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EntryPageComponent } from './entry-page.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('EntryPageComponent', () => {
  let component: EntryPageComponent;
  let fixture: ComponentFixture<EntryPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EntryPageComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EntryPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
