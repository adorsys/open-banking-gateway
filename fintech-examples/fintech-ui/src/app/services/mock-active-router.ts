import { Params } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

export class MockActivatedRoute {
  public innerTestParams?: any;
  public subject?: BehaviorSubject<any> = new BehaviorSubject(this.testParams);

  params = this.subject.asObservable();
  paramMap = this.subject.asObservable();
  queryParams = this.subject.asObservable();

  constructor(params?: Params) {
    if (params) {
      this.testParams = params;
    } else {
      this.testParams = {};
    }
  }

  get testParams() {
    return this.innerTestParams;
  }

  set testParams(params: {}) {
    this.innerTestParams = params;
    this.subject.next(params);
  }

  get(id: string) {
    return this.paramMap.subscribe(Id => {
      id = Id;
    });
  }

  get snapshot() {
    return { paramMap: this.paramMap, queryParams: this.testParams };
  }
}
