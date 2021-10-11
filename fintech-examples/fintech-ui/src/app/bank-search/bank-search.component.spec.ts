import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BankSearchComponent, BankSearchInfo } from './bank-search.component';
import { SearchComponent } from '../common/search/search.component';
import { BankSearchService } from './services/bank-search.service';
import { StorageService } from '../services/storage.service';
import { Router } from '@angular/router';
import { RoutingPath } from '../models/routing-path.model';

describe('BankSearchComponent', () => {
  let component: BankSearchComponent;
  let fixture: ComponentFixture<BankSearchComponent>;
  let bankSearchService: BankSearchService;
  let storageService: StorageService;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      declarations: [BankSearchComponent, SearchComponent],
      providers: []
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BankSearchComponent);
    bankSearchService = TestBed.inject(BankSearchService);
    storageService = TestBed.inject(StorageService);
    router = TestBed.inject(Router);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call onSearch', () => {
    const keyword = 'adorsys';

    const onSearchSpy = spyOn(component, 'onSearch').withArgs(keyword).and.callThrough();
    component.onSearch(keyword);
    expect(onSearchSpy).toHaveBeenCalled();
  });

  it('should call onBankSelect', () => {
    const mockBank: BankSearchInfo = new BankSearchInfo('bank1', '123');

    const routerSpy = spyOn(router, 'navigate');
    spyOn(component, 'onBankSelect').withArgs(mockBank).and.callThrough();
    component.onBankSelect(mockBank);
    expect(routerSpy).toHaveBeenCalledWith([RoutingPath.BANK, mockBank.uuid]);
    expect(component.selectedBankProfile).toEqual(mockBank.uuid);
  });
});
