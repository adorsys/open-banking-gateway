import { FinTechAccountInformationService } from '../../api';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AisService } from './ais.service';
import { LoARetrievalInformation, LoTRetrievalInformation } from '../../models/consts';
import {StorageService} from "../../services/storage.service";

describe('AisService', () => {
  let finTechAccountInformationService: FinTechAccountInformationService;
  let aisService: AisService;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AisService]
    });

    finTechAccountInformationService = TestBed.inject(FinTechAccountInformationService);
    aisService = TestBed.inject(AisService);
  });

  it('should be created', () => {
    expect(aisService).toBeTruthy();
  });

  it('should get accounts', () => {
    const getAccountsSpy = spyOn(aisService, 'getAccounts');
    aisService.getAccounts('1234', LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT, '', true, false, true);
    expect(getAccountsSpy).toHaveBeenCalled();
  });

  it('should get transactions', () => {
    const getTransactionsSpy = spyOn(aisService, 'getTransactions');
    aisService.getTransactions(
      '1234',
      'xxxxxxxxxx',
      LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT,
      '',
      false,
      true,
      '1970-01-01',
      StorageService.isoDate(new Date())
    );
    expect(getTransactionsSpy).toHaveBeenCalled();
  });
});
