package de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation;

import com.google.common.io.Resources;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class JsonTemplateInterpolation {

    @SneakyThrows
    public String interpolate(String templateResourcePath, SandboxContext context) {
        return Resources.asByteSource(Resources.getResource(templateResourcePath)).asCharSource(StandardCharsets.UTF_8).read();
    }
}
