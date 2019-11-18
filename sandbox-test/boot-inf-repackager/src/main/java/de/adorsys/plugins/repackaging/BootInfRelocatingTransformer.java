package de.adorsys.plugins.repackaging;

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarOutputStream;

public class BootInfRelocatingTransformer implements ResourceTransformer {

    @Override
    public boolean canTransformResource(String s) {
        System.out.println("Can transform: " + s);
        return false;
    }

    @Override
    public void processResource(String s, InputStream inputStream, List<Relocator> list) throws IOException {

    }

    @Override
    public boolean hasTransformedResource() {
        return false;
    }

    @Override
    public void modifyOutputStream(JarOutputStream jarOutputStream) throws IOException {

    }
}
