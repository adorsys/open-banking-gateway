import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswordInputPageComponent } from './password-input-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

describe('PasswordInputPageComponent', () => {
  let component: PasswordInputPageComponent;
  let fixture: ComponentFixture<PasswordInputPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PasswordInputPageComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { parent: { snapshot: { paramMap: convertToParamMap({ authId: 'AUTH-ID' }) } } }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PasswordInputPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
