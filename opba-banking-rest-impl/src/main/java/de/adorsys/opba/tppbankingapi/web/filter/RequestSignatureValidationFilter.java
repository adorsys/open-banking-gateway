package de.adorsys.opba.tppbankingapi.web.filter;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.facade.services.RequestVerifyingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestSignatureValidationFilter extends OncePerRequestFilter {
    private static final String X_REQUEST_SIGNATURE = "X-Request-Signature";

    private final RequestVerifyingService requestVerifyingService;
    private final FintechRepository fintechRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Long fintechId = 1L;

        Optional<Fintech> fintech = fintechRepository.findById(fintechId);

        byte[] publicKey = fintech.map(Fintech::getApiKeys)
                                   .orElse(null);

        if (publicKey == null) {
            log.warn("Public key for fintech ID {} has not find ", fintechId);
            return;
        }

        String xRequestSignature = request.getHeader(X_REQUEST_SIGNATURE);
        String signData = requestVerifyingService.verify(xRequestSignature, new String(publicKey));

        System.out.println(signData);

        //Optional<FintechRequest> requestById = fintechRequestRepository.findByXRequestId(xRequestIdDecr);

        /*if (requestById.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong X-Request-Id");
            return;
        }*/

        filterChain.doFilter(request, response);
    }
}
