import { TestBed } from '@angular/core/testing';

import { DocumentCookieService } from './document-cookie.service';

describe('DocumentCookieService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DocumentCookieService = TestBed.inject(DocumentCookieService);
    expect(service).toBeTruthy();
  });
});
