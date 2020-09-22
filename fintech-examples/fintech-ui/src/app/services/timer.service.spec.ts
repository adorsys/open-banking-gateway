import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TimerService } from './timer.service';

describe('TimerService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule]
    })
  );

  it('should be created', () => {
    const service: TimerService = TestBed.inject(TimerService);
    expect(service).toBeTruthy();
  });
});
