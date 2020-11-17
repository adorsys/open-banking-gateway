import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmComponent } from './confirm.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { ConfirmData } from './confirm.data';
import { RedirectStruct } from '../../redirect-page/redirect-struct';
import { ClassSinglePaymentInitiationRequest } from '../../../api/model-classes/ClassSinglePaymentInitiationRequest';

describe('ConfirmComponent', () => {
  let component: ConfirmComponent;
  let fixture: ComponentFixture<ConfirmComponent>;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [ConfirmComponent]
    })
      .overrideComponent(ConfirmComponent, {
        set: {
          providers: [
            {
              provide: ActivatedRoute,
              useValue: {
                paramMap: {
                  subscribe(confirmationData: string): string {
                    const r: RedirectStruct = new RedirectStruct();
                    r.bankName = 'peter';
                    r.redirectUrl = 'redirectUrl';
                    const p: ClassSinglePaymentInitiationRequest = new ClassSinglePaymentInitiationRequest();
                    p.debitorIban = 'DE80760700240271232400';
                    p.creditorIban = 'AL90208110080000001039531801';
                    p.amount = '1.10';
                    p.name = 'peter';
                    const c: ConfirmData = new ConfirmData();
                    c.redirectStruct = r;
                    c.paymentRequest = p;
                    return JSON.stringify(c);
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
    fixture = TestBed.createComponent(ConfirmComponent);
    component = fixture.componentInstance;
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

  it('amount of money to be wired should be rounded properly', () => {
    const numb = 2122;
    const rest = 2122.0;
    spyOn(component, 'roundToTwoDigitsAfterComma').withArgs(numb).and.callThrough();
    expect(numb).toEqual(rest);
  });
});
