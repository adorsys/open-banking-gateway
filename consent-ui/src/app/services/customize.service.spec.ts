import { TestBed } from '@angular/core/testing';

import { CustomizeService } from './customize.service';

describe('CustomizeService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CustomizeService = TestBed.inject(CustomizeService);
    expect(service).toBeTruthy();
  });
});
