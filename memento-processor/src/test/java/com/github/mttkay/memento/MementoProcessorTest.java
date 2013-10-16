package com.github.mttkay.memento;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import com.google.testing.compile.CompilationFailureException;
import com.google.testing.compile.JavaFileObjects;
import com.sun.tools.internal.xjc.util.NullStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.truth0.Truth;

import java.io.PrintStream;

@RunWith(JUnit4.class)
public class MementoProcessorTest {

    @Before
    public void dontPrintExceptions() {
        // get rid of the stack trace prints for expected exceptions
        System.setErr(new PrintStream(new NullStream()));
    }

    @Test
    public void itGeneratesMementoFragmentClass() {
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("RetainedActivity.java"))
                .processedWith(new MementoProcessor())
                .compilesWithoutError()
                .and().generatesSources(JavaFileObjects.forResource("RetainedActivity$Memento.java"));
    }

    @Test(expected = CompilationFailureException.class)
    public void itThrowsExceptionWhenRetainedFieldIsPrivate() {
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("RetainedActivityWithPrivateFields.java"))
                .processedWith(new MementoProcessor())
                .failsToCompile();
    }
}
