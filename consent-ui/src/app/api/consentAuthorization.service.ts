import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

/**
 * TODO: Replace this with generated one.
 */

@Injectable({
  providedIn: 'root'
})
export class ConsentAuthorizationService {

  private basePath = 'http://localhost:4200/embedded-server';

  constructor(protected httpClient: HttpClient) {
  }

  public authUsingGET(authId: string, redirectCode?: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
    if (authId === null || authId === undefined) {
      throw new Error('Required parameter authId was null or undefined when calling authUsingGET.');
    }

    let queryParameters = new HttpParams();
    if (redirectCode !== undefined && redirectCode !== null) {
      queryParameters = queryParameters.set('redirectCode', redirectCode);
    }

    let headers = new HttpHeaders();
    return this.httpClient.get(`${this.basePath}/v1/consent/${encodeURIComponent(String(authId))}`,
      {
        params: queryParameters,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }

  public embeddedUsingPOST(authId: string, xRequestID: string, X_XSRF_TOKEN: string, redirectCode?: string,
                           psuAuthRequest?: any, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
    if (authId === null || authId === undefined) {
      throw new Error('Required parameter authId was null or undefined when calling embeddedUsingPOST.');
    }
    if (xRequestID === null || xRequestID === undefined) {
      throw new Error('Required parameter xRequestID was null or undefined when calling embeddedUsingPOST.');
    }
    if (X_XSRF_TOKEN === null || X_XSRF_TOKEN === undefined) {
      throw new Error('Required parameter X_XSRF_TOKEN was null or undefined when calling embeddedUsingPOST.');
    }

    let queryParameters = new HttpParams();
    if (redirectCode !== undefined && redirectCode !== null) {
      queryParameters = queryParameters.set('redirectCode', redirectCode);
    }

    let headers = new HttpHeaders();
    return this.httpClient.post(
      `${this.basePath}/v1/consent/${encodeURIComponent(String(authId))}/embedded`,
      psuAuthRequest,
      {
        params: queryParameters,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }
}

export interface ConsentAuthField {

  uiCode: string;
  ctxCode: string;
  caption: string;
  type: string;
}
