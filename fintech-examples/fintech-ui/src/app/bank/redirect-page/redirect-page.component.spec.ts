import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';

import { RedirectPageComponent } from './redirect-page.component';
import { RedirectCardComponent } from '../redirect-card/redirect-card.component';
import { ConsentAuthorizationService } from '../services/consent-authorization.service';
import { RedirectStruct } from './redirect-struct';

describe('RedirectPageComponent', () => {
  let component: RedirectPageComponent;
  let fixture: ComponentFixture<RedirectPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [RedirectPageComponent, RedirectCardComponent]
    })
      .overrideComponent(RedirectPageComponent, {
        set: {
          providers: [
            {
              provide: ActivatedRoute,
              useValue: {
                paramMap: {
                  subscribe(location: string): string {
                    const r: RedirectStruct = new RedirectStruct();
                    r.bankName = 'peter';
                    r.redirectUrl = 'redirectUrl';
                    return JSON.stringify(r);
                  }
                }
              }
            },
            {
              provide: ConsentAuthorizationService
            }
          ]
        }
      })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
