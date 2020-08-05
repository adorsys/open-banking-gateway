package de.adorsys.opba.api.security.generator.api;

/**
 * Provides converter to map request to its string canonical form.
 * String canonical form of the request data is (in order):
 * <ol>
 * <li>request path + '&' delimiter</li>
 * <li>request headers in alphabetical order each with '&' delimiter</li>
 * <li>request query parameters in alphabetical order each with + '&' delimiter</li>
 * <li>request body</li>
 * </ol>
 *
 * For example:
 * <ul>
 * <li>POST /payment?auth=8799879798&from=anton.brueckner</li>
 * <li>Headers:</li>
 * <li>Amount=13.00</li>
 * <li>SourceIban=1231242314</li>
 * <li>Body:</li>
 * <li>&lt;payment&gt;&lt;id&gt;1234&lt;/id&gt;&lt;/payment&gt;</li>
 * </ul>
 *
 * Has canonical string:
 * <ul>
 * <li>/payment&Amount=13.00&SourceIban=1231242314&auth=8799879798&from=anton.brueckner&body=&lt;payment&gt;&lt;id&gt;1234&lt;/id&gt;&lt;/payment&gt</li>
 * </ul>
 *
 * Another example:
 * <ul>
 * <li>POST /payment?auth=8799879798&from=anton.brueckner</li>
 * <li>Headers:</li>
 * <li>Amount=13.00</li>
 * <li>SourceIban=1231242314</li>
 * </ul>
 *
 * Has canonical string:
 * <ul>
 * <li>/payment&Amount=13.00&SourceIban=1231242314&auth=8799879798&from=anton.brueckner&
 * </ul>
 *
 * Short canonical form of the request data is:
 * Note: Technically hash strength other than collision resistance is not of much importance here as the value
 * is going to be signed with JWS
 */
public interface DataToSignProvider {

    /**
     * Changes base request path. For example, consider that endpoint is running at
     * http://example.com/open-banking/payments
     * and target endpoint is /payments - we don't want to include 'open-banking' in request signature.
     * Then you can do withBasePath("/open-banking") to get desired result.
     * @param basePath Request base path
     * @return Converter that will ignore base path segment
     */
    DataToSignProvider withBasePath(String basePath);

    /**
     * Returns signer for the given request that can compute canonical request string
     * @param toSign Request to sign
     * @return Signer for the request
     */
    RequestDataToSignGenerator normalizerFor(RequestToSign toSign);

    enum HttpMethod {
        POST,
        GET,
        PUT,
        PATCH,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE;
    }
}
