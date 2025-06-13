import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { InitiateComponent } from './initiate.component';

import { StorageService } from '../../../services/storage.service';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { RouteUtilsService } from '../../../services/route-utils.service';

describe('InitiateComponent', () => {
  let component: InitiateComponent;
  let fixture: ComponentFixture<InitiateComponent>;
  let route: ActivatedRoute;
  let bankId;
  let routeUtils: RouteUtilsService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterTestingModule, InitiateComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                params: { bankid: '1234', accountid: '1234' },
                queryParams: { iban: 'AL90208110080000001039531801' }
              }
            }
          },
          provideHttpClient(withInterceptorsFromDi()),
          provideHttpClientTesting()
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(InitiateComponent);
    component = fixture.componentInstance;
    route = TestBed.inject(ActivatedRoute);
    routeUtils = TestBed.inject(RouteUtilsService);
    bankId = routeUtils.getBankId(route);
    const storageService = TestBed.inject(StorageService);
    spyOn(storageService, 'getLoa')
      .withArgs(bankId)
      .and.returnValue([{ resourceId: '1234', iban: '2', name: '3' }]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call onDeny', () => {
    const onDenySpy = spyOn(component, 'onDeny');
    component.onDeny();
    expect(onDenySpy).toHaveBeenCalled();
  });

  it('should call onConfirm', () => {
    const onConfirmSpy = spyOn(component, 'onConfirm');
    component.onConfirm();
    expect(onConfirmSpy).toHaveBeenCalled();
  });
});
