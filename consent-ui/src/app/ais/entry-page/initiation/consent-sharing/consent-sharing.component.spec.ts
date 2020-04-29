import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsentSharingComponent } from './consent-sharing.component';
import { ActivatedRoute } from '@angular/router';
import { StubUtilTests } from '../../../common/stub-util-tests';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ConsentSharingComponent', () => {
  let component: ConsentSharingComponent;
  let fixture: ComponentFixture<ConsentSharingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConsentSharingComponent],
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              snapshot: { params: { authId: StubUtilTests.AUTH_ID } }
            },
            snapshot: {
              queryParams: {}
            }
          }
        }
      ]
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
