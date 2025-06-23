import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RouteUtilsService {
  findRouteParam(route: ActivatedRoute, paramName: string): string | null {
    let currentRoute = route;
    while (currentRoute) {
      if (currentRoute.snapshot.params[paramName]) {
        return currentRoute.snapshot.params[paramName];
      }
      currentRoute = currentRoute.parent;
    }
    return null;
  }

  getBankId(route: ActivatedRoute): string | null {
    return this.findRouteParam(route, 'bankid');
  }

  getAccountId(route: ActivatedRoute): string | null {
    return this.findRouteParam(route, 'accountid');
  }
}
