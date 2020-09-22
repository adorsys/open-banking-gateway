import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BankSearchComponent } from './bank-search.component';
import { SearchComponent } from '../common/search/search.component';
import { BankDescriptor } from '../api';

fdescribe('BankSearchComponent', () => {
  let component: BankSearchComponent;
  let fixture: ComponentFixture<BankSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      declarations: [BankSearchComponent, SearchComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BankSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call onSearch', () => {
    const keyword = 'adorsys';
    const mockBankDescriptor: BankDescriptor[] = [];

    spyOn(component, 'onSearch').withArgs(keyword).and.callThrough();
    expect(component.searchedBanks).toEqual(mockBankDescriptor);
  });

  it('should call onBankSelect', () => {
    const mockBank: BankDescriptor = {};

    spyOn(component, 'onBankSelect').withArgs(mockBank).and.callThrough();
    expect(component.selectedBank).toEqual(mockBank.uuid);
  });
});
