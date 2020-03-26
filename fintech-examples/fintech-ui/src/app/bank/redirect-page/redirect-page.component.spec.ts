import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectPageComponent } from './redirect-page.component';
import { RedirectCardComponent } from '../redirect-card/redirect-card.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AisService } from '../services/ais.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

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
            AisService,
            {
              provide: ActivatedRoute,
              useValue: {
                params: of({ bankId: 1234 }),
                paramMap: {
                  subscribe(location: string): string {
                    return 'adorsys.de';
                  }
                }
              }
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
