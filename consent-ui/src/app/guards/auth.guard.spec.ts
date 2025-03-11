import { inject, TestBed } from '@angular/core/testing';

import { AuthGuard } from './auth.guard';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('AuthGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [AuthGuard, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
    });
  });

  it('should ...', inject([AuthGuard], (guard: AuthGuard) => {
    expect(guard).toBeTruthy();
  }));
});
