import { inject, TestBed } from '@angular/core/testing';

import { AuthGuard } from './auth.guard';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('AuthGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthGuard]
    });
  });

  it('should ...', inject([AuthGuard], (guard: AuthGuard) => {
    expect(guard).toBeTruthy();
  }));
});
