package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastRedirectionTarget {

    private String redirectTo;
    private String redirectToUiScreen;
}
