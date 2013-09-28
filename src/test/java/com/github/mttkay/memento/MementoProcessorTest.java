package com.github.mttkay.memento;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import com.google.testing.compile.CompilationFailureException;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.truth0.Truth;

@RunWith(JUnit4.class)
public class MementoProcessorTest {

    @Test
    public void generateMementoClass() {
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("RetainedActivity.java"))
                .processedWith(new MementoProcessor())
                .compilesWithoutError()
                .and().generatesSources(JavaFileObjects.forResource("RetainedActivity_Memento.java"));
    }

    @Test(expected = CompilationFailureException.class)
    public void throwsIllegalStateIfRetainedFieldIsPrivate() {
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("RetainedActivityWithPrivateFields.java"))
                .processedWith(new MementoProcessor())
                .failsToCompile();
    }
}
