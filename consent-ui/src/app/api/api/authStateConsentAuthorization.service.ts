/**
 * Open Banking Gateway - Consent Authorization API.
 * Interface used by the PsuUserAgent to present consent authorization services to the PSU. The consent authorization process is triggered by redirecting the PSU from the [TppBankingApi](https://adorsys.github.io/open-banking-gateway/doc/latest/architecture/dictionary#TppBankingApi) (2<sub>a</sub>) over the [FinTechApi](https://adorsys.github.io/open-banking-gateway/doc/latest/architecture/dictionary#FinTechApi) (2<sub>b</sub>) to the /consent/{auth-id} entry point of this [ConsentAuthorisationApi](https://adorsys.github.io/open-banking-gateway/doc/latest/architecture/dictionary#ConsentAuthorisationApi) (2<sub>c</sub>). The decision on whether the authorization process is embedded or redirected is taken by this ConsentAuthorisationApi.  The following picture displays the overall architecture of this open banking consent authorisation api:   ![High level architecture](/img/open-banking-consent-authorisation-api.png)   #### User Agent This Api assumes that the PsuUserAgent is a modern browsers that : * automatically detects the \"302 Found\" response code and proceeds with the associated location url, * stores httpOnly cookies sent with the redirect under the given domain and path as defined by [RFC 6265](https://tools.ietf.org/html/rfc6265).  This Api also assumes any other PsuUserAgent like a native mobile or a desktop application can simulate this same behavior of amodern browser with respect to 30X and Cookies.    #### Redirecting to the ConsentAuthorisationApi (2<sub>a</sub>) Any service request of the FinTechUI to the FinTechApi (1<sub>a</sub>) will be forwarded to the TppBankingApi (1<sub>b</sub>). This forward might contain a [PsuConsentSession](https://adorsys.github.io/open-banking-gateway/doc/latest/architecture/dictionary#PsuConsentSession) that is used to identify the PSU in the world of the TPP.  The TppBankingApi uses the provided PsuConsentSession to retrieve an eventualy suitable consent that will be used to forward the corresponding service request to the OpenBankingApi (1<sub>c</sub>) of the ASPSP. If there is no suitable consent, the TPP might still send a consent initiation request to the OpenBankingApi (1<sub>c</sub>). Whether this request is sent or not depends on the design of the target OpenBankingApi. Finally, the TppBankingApi will if necessary instruct the FinTechApi (2<sub>a</sub>) to redirect the PsuUgerAgent (2<sub>b</sub>) to the /consent/{auth-id} entry point of  the ConsentAuthorisationApi (2<sub>c</sub>).      #### Issolation Authorisation Request Processing The auth-id parameter is used to make sure paralell authorization requests are not mixup.      #### SessionCookies and XSRF Each authorisation session started will be associated with a proper SessionCookie and a corresponding XSRF-TOKEN. * The request that sets a session cookie (E<sub>1</sub>) also add the X-XSRF-TOKEN to the response header. * The cookie path is always extended with the corresponding auth-id, so two Authorization processes can not share state.  * Each authenticated request sent to the ConsentAuthorisationApi will provide the X-XSRF-TOKEN matching the sent SessionCookie.  #### RedirectCookie and XSRF (R<sub>1</sub>) In a redirect approach (Redirecting PSU to the ASPSP), the The retruned AuthorizeResponse object contains information needed to present a suitable redirect info page to the PSU. Redirection can either be actively performed by the UIApplication or performed as a result of a 30x redirect response to the PsuUserAgent. In both cases, a RedirectCookie will be associated with the  PsuUserAgent and a corresponding XSRF-TOKEN named redirectState will be addedto the back redirect url.      #### Final Result of the Authorization Process The final result of the authorization process is a PsuCosentSession that is returned by the token endpoint of the TppBankingAPi to the FinTechApi (4<sub>c</sub>). This handle will (PsuCosentSession) will be stored by the FinTechApi and added a PSU identifying information to each service request associated with this PSU.
 *
 * The version of the OpenAPI document: 1.0
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

import { ConsentAuth } from '../model/models';
import { PsuMessage } from '../model/models';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';



@Injectable({
  providedIn: 'root'
})
export class AuthStateConsentAuthorizationService {

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
     * Redirect entry point for initiating a consent authorization process.
     * This is the &lt;b&gt;entry point&lt;/b&gt; for processing a consent redirected by the TppBankingApi to this ConsentAuthorisationApi.  At this entry point, the ConsentAuthorisationApi will use the redirectCode to retrieve the RedirectSession from the TppServer. An analysis of the RedirectSession will help decide if the ConsentAuthorisationApi will proceed with an embedded approach (E&lt;sub&gt;1&lt;/sub&gt;) or a redirect approach (R&lt;sub&gt;1&lt;/sub&gt;).
     * @param authId Used to distinguish between different consent authorization processes started by the same PSU. Also included in the corresponding cookie path to limit visibility of the consent cookie to the corresponding consent process.
     * @param redirectCode Code used to retrieve a redirect session. This is generaly transported as a query parameter
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public authUsingGET(authId: string, redirectCode?: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<ConsentAuth>;
    public authUsingGET(authId: string, redirectCode?: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<ConsentAuth>>;
    public authUsingGET(authId: string, redirectCode?: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<ConsentAuth>>;
    public authUsingGET(authId: string, redirectCode?: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        if (authId === null || authId === undefined) {
            throw new Error('Required parameter authId was null or undefined when calling authUsingGET.');
        }

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (redirectCode !== undefined && redirectCode !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>redirectCode, 'xXsrfToken');
        }

        let headers = this.defaultHeaders;

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

        return this.httpClient.get<ConsentAuth>(`${this.configuration.basePath}/v1/consent/${encodeURIComponent(String(authId))}`,
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
