import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BankSearchComponent, BankSearchInfo } from './bank-search.component';
import { SearchComponent } from '../common/search/search.component';
import { Router } from '@angular/router';
import { RoutingPath } from '../models/routing-path.model';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('BankSearchComponent', () => {
  let component: BankSearchComponent;
  let fixture: ComponentFixture<BankSearchComponent>;
  let router: Router;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterTestingModule, BankSearchComponent, SearchComponent],
        providers: [provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(BankSearchComponent);
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
