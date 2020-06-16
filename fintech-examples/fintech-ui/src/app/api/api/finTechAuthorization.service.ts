/**
 * Open Banking Gateway FinTech Example API
 * This is a sample API that shows how to develop FinTech use cases that invoke banking APIs.  #### User Agent and Cookies This Api assumes  * that the PsuUserAgent (hosting the FinTechUI) is a modern web browser that stores httpOnly cookies sent with the redirect under the given domain and path as defined by [RFC 6265](https://tools.ietf.org/html/rfc6265). * that any other PsuUserAgent like a native mobile or a desktop application can simulate this same behavior of a modern browser with respect to Cookies.  #### SessionCookies and XSRF After a PSU is authenticated with the FinTech environment (either through the simple login interface defined here, or through an identity provider), the FinTechApi will establish a session with the FinTechUI. This is done by the mean of using a cookie called SessionCookie. This SessionCookie is protected by a corresponding xsrfToken. The response that sets a SessionCookie also carries a corresponding xsrfToken in the response header named \"X-XSRF-TOKEN\".  It is the responsibility of the FinTechUI to : * parse and store this xsrfToken so that a refresh of a browser window can work. This shall be done using user agent capabilities. A web browser application might decide to store the xsrfToken in the browser localStorage, as the cookie we set are all considered persistent. * make sure that each subsequent request that is carrying the SessionCookie also carries the corresponding xsrfToken as header field (see the request path). * remove this xsrfToken from the localStorage when the corresponding SessionCookie is deleted by a server response (setting cookie value to null).  The main difference between an xsrfToken and a SessionCookie is that the sessionCookie is automatically sent with each matching request. The xsrfToken must be explicitely read and sent by application.  #### API- vs. UI-Redirection For simplicity, this Framework is designed to redirect to FinTechUI not to FinTechApi.  #### Explicite vs. Implicite Redirection We define an \"Implicite redirection\" a case where a web browser react to 30X reponse and automatically redirects to the attached endpoint. We define an \"Explicite Redirection\" as a case where the UI-Application reacts to a 20X response, explicitely parses the attached __Location__ header an uses it to reload the new page in the browser window (or start the new UI-Application in case of native apps).  This framework advocates for explicite redirection passing a __20X__ response to the FinTechUI toghether with the __Location__ parameter.  Processing a response that initiates a redirect, the FinTechUI makes sure following happens, * that the exisitng __SessionCookie__ is deleted, as the user will not have a chance for an explicite logout, * that the corresponding xsrfToken is deleted from the local storage, * that a RedirectCookie set is stored (in case UI is not a web browser), so the user can be authenticated against it when sent back to the FinTechUI. The expiration of the RedirectCookie shall be set to the expected duration of the redirect, * that the corresponding xsrfToken is stored in the local storage (under the same cookie path as the RedirectCookie)  #### Redirecting to the ConsentAuthorisationApi For a redirection to the ConsentAuthorisationApi, a generated AUTH-ID is added to the cookie path and used to distinguish authorization processes from each order. This information (AUTH-ID) must be contained in the back redirect url sent to the ConsentAuthorisationApi in the back channel, so that the FinTechUI can invoke the correct code2Token endpoint when activated. 
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

import { ErrorResponse } from '../model/errorResponse';
import { InlineResponse200 } from '../model/inlineResponse200';
import { LoginRequest } from '../model/loginRequest';
import { PsuMessage } from '../model/psuMessage';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';



@Injectable({
  providedIn: 'root'
})
export class FinTechAuthorizationService {

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



    /**
     * Entry point when PSU is redirected back from ConsentAuthorisationApi to the FinTechUI.
     * Entry point when PSU is redirected back from ConsentAuthorisationApi to the FinTechUI. 
     * @param authId 
     * @param okOrNotok 
     * @param redirectCode 
     * @param xRequestID Unique ID that identifies this request through common workflow. Must be contained in HTTP Response as well. 
     * @param X_XSRF_TOKEN XSRF parameter used to validate a SessionCookie or RedirectCookie. 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public fromConsentGET(authId: string, okOrNotok: 'OK' | 'NOT_OK', redirectCode: string, xRequestID: string, X_XSRF_TOKEN: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public fromConsentGET(authId: string, okOrNotok: 'OK' | 'NOT_OK', redirectCode: string, xRequestID: string, X_XSRF_TOKEN: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public fromConsentGET(authId: string, okOrNotok: 'OK' | 'NOT_OK', redirectCode: string, xRequestID: string, X_XSRF_TOKEN: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public fromConsentGET(authId: string, okOrNotok: 'OK' | 'NOT_OK', redirectCode: string, xRequestID: string, X_XSRF_TOKEN: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (authId === null || authId === undefined) {
            throw new Error('Required parameter authId was null or undefined when calling fromConsentGET.');
        }
        if (okOrNotok === null || okOrNotok === undefined) {
            throw new Error('Required parameter okOrNotok was null or undefined when calling fromConsentGET.');
        }
        if (redirectCode === null || redirectCode === undefined) {
            throw new Error('Required parameter redirectCode was null or undefined when calling fromConsentGET.');
        }
        if (xRequestID === null || xRequestID === undefined) {
            throw new Error('Required parameter xRequestID was null or undefined when calling fromConsentGET.');
        }
        if (X_XSRF_TOKEN === null || X_XSRF_TOKEN === undefined) {
            throw new Error('Required parameter X_XSRF_TOKEN was null or undefined when calling fromConsentGET.');
        }

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (redirectCode !== undefined && redirectCode !== null) {
            queryParameters = queryParameters.set('redirectCode', <any>redirectCode);
        }

        let headers = this.defaultHeaders;
        if (xRequestID !== undefined && xRequestID !== null) {
            headers = headers.set('X-Request-ID', String(xRequestID));
        }
        if (X_XSRF_TOKEN !== undefined && X_XSRF_TOKEN !== null) {
            headers = headers.set('X-XSRF-TOKEN', String(X_XSRF_TOKEN));
        }

        // to determine the Accept header
        const httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        return this.httpClient.get<any>(`${this.configuration.basePath}/v1/${encodeURIComponent(String(authId))}/fromConsent/${encodeURIComponent(String(okOrNotok))}`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Identifies the PSU in the Realm of the FinTechApi.
     * Simple login interface used to establish a session between PSU and FinTech. Real application will delegate login to an oAuth2 Identity provider. 
     * @param xRequestID Unique ID that identifies this request through common workflow. Must be contained in HTTP Response as well. 
     * @param loginRequest Login request
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public loginPOST(xRequestID: string, loginRequest: LoginRequest, observe?: 'body', reportProgress?: boolean): Observable<InlineResponse200>;
    public loginPOST(xRequestID: string, loginRequest: LoginRequest, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<InlineResponse200>>;
    public loginPOST(xRequestID: string, loginRequest: LoginRequest, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<InlineResponse200>>;
    public loginPOST(xRequestID: string, loginRequest: LoginRequest, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (xRequestID === null || xRequestID === undefined) {
            throw new Error('Required parameter xRequestID was null or undefined when calling loginPOST.');
        }
        if (loginRequest === null || loginRequest === undefined) {
            throw new Error('Required parameter loginRequest was null or undefined when calling loginPOST.');
        }

        let headers = this.defaultHeaders;
        if (xRequestID !== undefined && xRequestID !== null) {
            headers = headers.set('X-Request-ID', String(xRequestID));
        }

        // to determine the Accept header
        const httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
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

        return this.httpClient.post<InlineResponse200>(`${this.configuration.basePath}/v1/login`,
            loginRequest,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * logs out user
     * If user can be authenticated, user will be logged out.
     * @param xRequestID Unique ID that identifies this request through common workflow. Must be contained in HTTP Response as well. 
     * @param X_XSRF_TOKEN XSRF parameter used to validate a SessionCookie or RedirectCookie. 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public logoutPOST(xRequestID: string, X_XSRF_TOKEN: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public logoutPOST(xRequestID: string, X_XSRF_TOKEN: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public logoutPOST(xRequestID: string, X_XSRF_TOKEN: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public logoutPOST(xRequestID: string, X_XSRF_TOKEN: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (xRequestID === null || xRequestID === undefined) {
            throw new Error('Required parameter xRequestID was null or undefined when calling logoutPOST.');
        }
        if (X_XSRF_TOKEN === null || X_XSRF_TOKEN === undefined) {
            throw new Error('Required parameter X_XSRF_TOKEN was null or undefined when calling logoutPOST.');
        }

        let headers = this.defaultHeaders;
        if (xRequestID !== undefined && xRequestID !== null) {
            headers = headers.set('X-Request-ID', String(xRequestID));
        }
        if (X_XSRF_TOKEN !== undefined && X_XSRF_TOKEN !== null) {
            headers = headers.set('X-XSRF-TOKEN', String(X_XSRF_TOKEN));
        }

        // authentication (sessionCookie) required
        // to determine the Accept header
        const httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        return this.httpClient.post<any>(`${this.configuration.basePath}/v1/logout`,
            null,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
