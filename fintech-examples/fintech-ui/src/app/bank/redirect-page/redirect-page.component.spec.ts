import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';


import {RedirectPageComponent} from './redirect-page.component';
import {RedirectCardComponent} from '../redirect-card/redirect-card.component';
import {StorageService} from '../../services/storage.service';
import {ConsentAuthorizationService} from '../services/consent-authorization.service';

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
                params: of({location: 'adorsys.de'}),
                paramMap: {
                  subscribe(location: string): string {
                    return 'adorsys.de';
                  }
                }
              }
            },
            {
              provide: StorageService,
              useValue: {
                getBankName(): string {
                  return 'peters bank';

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

  /* TODO Hergie help me
  it('should create', () => {
   expect(component).toBeTruthy();
  });
  */
});
