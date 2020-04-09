package de.adorsys.opba.protocol.facade.web.filter;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechRequest;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRequestRepository;
import de.adorsys.opba.protocol.facade.services.DataDecryptionService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

//TODO add filter to save each request in FintechRequest table and order after this?
@Component
public class RequestSignatureValidationFilter extends OncePerRequestFilter {

    private final DataDecryptionService dataDecryptionService;
    private final FintechRepository fintechRepository;
    private final FintechRequestRepository fintechRequestRepository;

    public RequestSignatureValidationFilter(DataDecryptionService dataDecryptionService, FintechRepository fintechRepository, FintechRequestRepository fintechRequestRepository) {
        this.dataDecryptionService = dataDecryptionService;
        this.fintechRepository = fintechRepository;
        this.fintechRequestRepository = fintechRequestRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //TODO How to recognize fintech
        Long fintechId = 1L;

        Optional<Fintech> fintech = fintechRepository.findById(fintechId);
        byte[] publicKey = fintech.map(Fintech::getApiKeys)
                                   .orElse(null);
        String xRequestIdEncr = request.getHeader("X-Request-ID");
        String xRequestIdDecr = dataDecryptionService.decrypt(xRequestIdEncr, publicKey);

        Optional<FintechRequest> requestById = fintechRequestRepository.findByXRequestId(xRequestIdDecr);

        if (requestById.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong X-Request-Id");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
