import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { InitiateComponent } from './initiate.component';

import { StorageService } from '../../../services/storage.service';
import { Consts } from '../../../models/consts';

describe('InitiateComponent', () => {
  let component: InitiateComponent;
  let fixture: ComponentFixture<InitiateComponent>;
  let route: ActivatedRoute;
  let bankId;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InitiateComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { params: { bankid: '1234', accountid: '1234' } }
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InitiateComponent);
    component = fixture.componentInstance;
    route = TestBed.get(ActivatedRoute);
    bankId = route.snapshot.params[Consts.BANK_ID_NAME];
    const storageService = TestBed.get(StorageService);
    spyOn(storageService, 'getLoa')
      .withArgs(bankId)
      .and.returnValue([{ resourceId: '1234', iban: '2', name: '3' }]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
