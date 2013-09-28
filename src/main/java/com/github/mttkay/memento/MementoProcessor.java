package com.github.mttkay.memento;

import com.squareup.javawriter.JavaWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

@SupportedAnnotationTypes("com.github.mttkay.memento.Retain")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class MementoProcessor extends AbstractProcessor {

    private static final String LIB_PACKAGE = "com.github.mttkay.memento";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Retain.class);
        if (elements.isEmpty()) {
            return true;
        }

        //TODO: verify activity implements onFirstCreate
        verifyFieldsAccessible(elements);

        log("processing " + elements.size() + " fields");

        Element hostActivity = elements.iterator().next().getEnclosingElement();
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(hostActivity);

        final String simpleClassName = hostActivity.getSimpleName() + "_Memento";
        final String qualifiedClassName = packageElement.getQualifiedName() + "." + simpleClassName;

        log("writing class " + qualifiedClassName);

        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(
                    qualifiedClassName, elements.toArray(new Element[elements.size()]));

            JavaWriter writer = new JavaWriter(sourceFile.openWriter());
            writer.emitPackage(packageElement.getQualifiedName().toString())
                    .emitImports("android.support.v4.app.Fragment")
                    .emitImports("android.support.v4.app.FragmentActivity")
                            //.emitImports(packageElement + ".*")
                    .emitEmptyLine();

            writer.beginType(qualifiedClassName, "class", EnumSet.of(Modifier.PUBLIC, Modifier.FINAL),
                    "Fragment", LIB_PACKAGE + ".MementoMethods");

            emitFields(elements, writer);
            emitConstructor(simpleClassName, writer);
            emitReaderMethod(elements, hostActivity, writer);
            emitWriterMethod(elements, hostActivity, writer);

            writer.endType();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException("Failed writing class file", e);
        }

        return true;
    }

    private void emitWriterMethod(Set<? extends Element> elements, Element hostActivity, JavaWriter writer) throws IOException {
        writer.emitEmptyLine();
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "to", EnumSet.of(Modifier.PUBLIC), "FragmentActivity", "target");
        final String activityClass = hostActivity.getSimpleName().toString();
        writer.emitStatement(activityClass + " activity = (" + activityClass + ") target");
        for (Element element : elements) {
            writer.emitStatement("activity." + element.getSimpleName() + " = this." + element.getSimpleName());
        }
        writer.endMethod();
    }

    private void emitReaderMethod(Set<? extends Element> elements, Element hostActivity, JavaWriter writer) throws IOException {
        writer.emitEmptyLine();
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "from", EnumSet.of(Modifier.PUBLIC), "FragmentActivity", "source");
        final String activityClass = hostActivity.getSimpleName().toString();
        writer.emitStatement(activityClass + " activity = (" + activityClass + ") source");
        for (Element element : elements) {
            writer.emitStatement("this." + element.getSimpleName() + " = activity." + element.getSimpleName());
        }
        writer.endMethod();
    }

    private void emitConstructor(String simpleClassName, JavaWriter writer) throws IOException {
        writer.emitEmptyLine();
        writer.beginMethod(null, simpleClassName, EnumSet.of(Modifier.PUBLIC))
                .emitStatement("setRetainInstance(true)")
                .endMethod();
    }

    private void emitFields(Set<? extends Element> elements, JavaWriter writer) throws IOException {
        writer.emitEmptyLine();
        for (Element element : elements) {
            writer.emitField(element.asType().toString(), element.getSimpleName().toString());
        }
    }

    private void verifyFieldsAccessible(Set<? extends Element> elements) {
        for (Element element : elements) {
            if (element.getModifiers().contains(Modifier.PRIVATE)) {
                throw new IllegalStateException("Annotated fields cannot be private: " +
                        element.getEnclosingElement() + "#" + element + "(" + element.asType() + ")");
            }
        }
    }

    private void log(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Memento: " + message);
    }
}
