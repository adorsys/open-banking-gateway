import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RestoreSessionComponent} from './restore-session.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('RestoreSessionComponent', () => {
  let component: RestoreSessionComponent;
  let fixture: ComponentFixture<RestoreSessionComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestoreSessionComponent ],
      imports: [HttpClientTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestoreSessionComponent);
    component = fixture.componentInstance;
    component.authId = '1-AUTH';
    component.aspspRedirectCode = '1-CODE';
    component.result = 'ok';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
