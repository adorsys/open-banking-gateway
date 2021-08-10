/**
 * Open Banking Gateway Tpp Authentication API
 * This API provides PSU login and registration functionality on TPP side.
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent, HttpParameterCodec }       from '@angular/common/http';
import { CustomHttpParameterCodec }                          from '../encoder';
import { Observable }                                        from 'rxjs';

import { GeneralError } from '../model/models';
import { LoginResponse } from '../model/models';
import { PsuAuthBody } from '../model/models';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';



@Injectable({
  providedIn: 'root'
})
export class PsuAuthenticationAndConsentApprovalService {

    protected basePath = 'http://localhost';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();
    public encoder: HttpParameterCodec;

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (configuration) {
            this.configuration = configuration;
        }
        if (typeof this.configuration.basePath !== 'string') {
            if (typeof basePath !== 'string') {
                basePath = this.basePath;
            }
            this.configuration.basePath = basePath;
        }
        this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
    }



    private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
        if (typeof value === "object" && value instanceof Date === false) {
            httpParams = this.addToHttpParamsRecursive(httpParams, value);
        } else {
            httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
        }
        return httpParams;
    }

    private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
        if (value == null) {
            return httpParams;
        }

        if (typeof value === "object") {
            if (Array.isArray(value)) {
                (value as any[]).forEach( elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
            } else if (value instanceof Date) {
                if (key != null) {
                    httpParams = httpParams.append(key,
                        (value as Date).toISOString().substr(0, 10));
                } else {
                   throw Error("key may not be null if value is Date");
                }
            } else {
                Object.keys(value).forEach( k => httpParams = this.addToHttpParamsRecursive(
                    httpParams, value[k], key != null ? `${key}.${k}` : k));
            }
        } else if (key != null) {
            httpParams = httpParams.append(key, value);
        } else {
            throw Error("key may not be null if value is not object or array");
        }
        return httpParams;
    }

    /**
     * Login user to open-banking to perform payment (anonymous to OPBA)
     * TBD
     * @param xRequestID Unique ID that identifies this request through common workflow. Shall be contained in HTTP Response as well. 
     * @param authorizationId Authorization session ID to approve
     * @param redirectCode Redirect code that acts as a password protecting FinTech requested consent specification
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public loginForAnonymousApproval(xRequestID: string, authorizationId: string, redirectCode: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<LoginResponse>;
    public loginForAnonymousApproval(xRequestID: string, authorizationId: string, redirectCode: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<LoginResponse>>;
    public loginForAnonymousApproval(xRequestID: string, authorizationId: string, redirectCode: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<LoginResponse>>;
    public loginForAnonymousApproval(xRequestID: string, authorizationId: string, redirectCode: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        if (xRequestID === null || xRequestID === undefined) {
            throw new Error('Required parameter xRequestID was null or undefined when calling loginForAnonymousApproval.');
        }
        if (authorizationId === null || authorizationId === undefined) {
            throw new Error('Required parameter authorizationId was null or undefined when calling loginForAnonymousApproval.');
        }
        if (redirectCode === null || redirectCode === undefined) {
            throw new Error('Required parameter redirectCode was null or undefined when calling loginForAnonymousApproval.');
        }

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (redirectCode !== undefined && redirectCode !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>redirectCode, 'redirectCode');
        }

        let headers = this.defaultHeaders;
        if (xRequestID !== undefined && xRequestID !== null) {
            headers = headers.set('X-Request-ID', String(xRequestID));
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.post<LoginResponse>(`${this.configuration.basePath}/v1/psu/${encodeURIComponent(String(authorizationId))}/for-approval/anonymous`,
            null,
            {
                params: queryParameters,
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Login user to open-banking
     * TBD
     * @param xRequestID Unique ID that identifies this request through common workflow. Shall be contained in HTTP Response as well. 
     * @param authorizationId Authorization session ID to approve
     * @param redirectCode Redirect code that acts as a password protecting FinTech requested consent specification
     * @param psuAuthBody User credentials object
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public loginForApproval(xRequestID: string, authorizationId: string, redirectCode: string, psuAuthBody: PsuAuthBody, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<LoginResponse>;
    public loginForApproval(xRequestID: string, authorizationId: string, redirectCode: string, psuAuthBody: PsuAuthBody, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<LoginResponse>>;
    public loginForApproval(xRequestID: string, authorizationId: string, redirectCode: string, psuAuthBody: PsuAuthBody, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<LoginResponse>>;
    public loginForApproval(xRequestID: string, authorizationId: string, redirectCode: string, psuAuthBody: PsuAuthBody, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        if (xRequestID === null || xRequestID === undefined) {
            throw new Error('Required parameter xRequestID was null or undefined when calling loginForApproval.');
        }
        if (authorizationId === null || authorizationId === undefined) {
            throw new Error('Required parameter authorizationId was null or undefined when calling loginForApproval.');
        }
        if (redirectCode === null || redirectCode === undefined) {
            throw new Error('Required parameter redirectCode was null or undefined when calling loginForApproval.');
        }
        if (psuAuthBody === null || psuAuthBody === undefined) {
            throw new Error('Required parameter psuAuthBody was null or undefined when calling loginForApproval.');
        }

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (redirectCode !== undefined && redirectCode !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>redirectCode, 'redirectCode');
        }

        let headers = this.defaultHeaders;
        if (xRequestID !== undefined && xRequestID !== null) {
            headers = headers.set('X-Request-ID', String(xRequestID));
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.post<LoginResponse>(`${this.configuration.basePath}/v1/psu/${encodeURIComponent(String(authorizationId))}/for-approval/login`,
            psuAuthBody,
            {
                params: queryParameters,
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
