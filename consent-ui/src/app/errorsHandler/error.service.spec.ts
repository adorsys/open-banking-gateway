import { TestBed } from '@angular/core/testing';

import { ErrorService } from './error.service';

describe('ErrorService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ErrorService = TestBed.inject(ErrorService);
    expect(service).toBeTruthy();
  });
});
