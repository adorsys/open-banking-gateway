import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { InitiateComponent } from './initiate.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('InitiateComponent', () => {
  let component: InitiateComponent;
  let fixture: ComponentFixture<InitiateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InitiateComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InitiateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // TODO PETER FIXME
  // it('should create', () => {
  //  expect(component).toBeTruthy();
  // });
});
