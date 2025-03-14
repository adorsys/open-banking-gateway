import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TimerService } from './timer.service';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('TimerService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
    imports: [RouterTestingModule],
    providers: [provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
})
  );

  it('should be created', () => {
    const service: TimerService = TestBed.inject(TimerService);
    expect(service).toBeTruthy();
  });
});
