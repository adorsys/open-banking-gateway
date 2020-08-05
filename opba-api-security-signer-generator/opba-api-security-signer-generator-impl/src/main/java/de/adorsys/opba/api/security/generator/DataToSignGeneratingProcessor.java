package de.adorsys.opba.api.security.generator;

import com.google.auto.service.AutoService;
import de.adorsys.opba.api.security.generator.api.GeneratedDataToSignNormalizer;
import de.adorsys.opba.api.security.generator.normalizer.RequestDataToSignNormalizerGenerator;
import de.adorsys.opba.api.security.generator.signer.DataToSignProviderGenerator;
import de.adorsys.opba.api.security.generator.signer.RequestDataToGeneratorGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes(DataToSignGeneratingProcessor.ANNOTATION_CLASS)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class DataToSignGeneratingProcessor extends AbstractProcessor {

    static final String ANNOTATION_CLASS = "de.adorsys.opba.api.security.generator.api.GeneratedDataToSignNormalizer";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        RequestDataToGeneratorGenerator signerGenerator = new RequestDataToGeneratorGenerator(new DataToSignProviderGenerator(new RequestDataToSignNormalizerGenerator()));
        for (TypeElement annotation : annotations) {
            // limit to elements annotated with {@link RuntimeDelegate}
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element annotated : annotatedElements) {
                if (annotated.getKind() != ElementKind.CLASS) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Only classes should be annotated with @" + ANNOTATION_CLASS,
                            annotated
                    );
                    return false;
                }

                TypeElement clazz = (TypeElement) annotated;

                signerGenerator.generate(
                        clazz,
                        annotated.getAnnotation(GeneratedDataToSignNormalizer.class),
                        super.processingEnv.getFiler()
                );
            }
        }

        return false;
    }
}
