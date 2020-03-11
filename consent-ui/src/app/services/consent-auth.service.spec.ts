import { TestBed } from '@angular/core/testing';

import { ConsentAuthService } from './consent-auth.service';

describe('ConsentAuthService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ConsentAuthService = TestBed.get(ConsentAuthService);
    expect(service).toBeTruthy();
  });
});
