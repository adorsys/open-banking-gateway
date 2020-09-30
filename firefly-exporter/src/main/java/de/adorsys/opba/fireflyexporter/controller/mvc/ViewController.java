package de.adorsys.opba.fireflyexporter.controller.mvc;

import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.service.ConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final ApiConfig apiConfig;
    private final ConsentService consentService;

    @GetMapping("/")
    public String fireFlyUploader(Model model) {
        return showFireflyUploaderScreen(model);
    }

    @GetMapping("/consent-confirmed")
    public String confirmConsent(@RequestParam("redirectCode") String redirectCode, Model model) {
        String bankId = consentService.confirmConsentAndGetBankId(redirectCode);
        model.addAttribute("bankId", bankId);
        return showFireflyUploaderScreen(model);
    }

    @GetMapping("/consent-declined")
    public String declineConsent(Model model) {
        return showFireflyUploaderScreen(model);
    }

    private String showFireflyUploaderScreen(Model model) {
        model.addAttribute("apiUrl", apiConfig.getUrl().toASCIIString());
        return "firefly-uploader";
    }
}

