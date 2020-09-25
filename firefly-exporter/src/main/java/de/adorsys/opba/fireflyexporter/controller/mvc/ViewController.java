package de.adorsys.opba.fireflyexporter.controller.mvc;

import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final ApiConfig apiConfig;

    @GetMapping("/")
    public String fireFlyUploader(Model model) {
        model.addAttribute("apiUrl", apiConfig.getUrl().toASCIIString());
        return "firefly-uploader";
    }
}

