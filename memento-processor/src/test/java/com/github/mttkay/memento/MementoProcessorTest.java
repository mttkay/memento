package com.github.mttkay.memento;

import static com.google.testing.compile.JavaFileObjects.forSourceString;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import com.google.common.base.Joiner;
import com.google.testing.compile.CompilationFailureException;
import com.google.testing.compile.JavaFileObjects;
import com.sun.tools.internal.xjc.util.NullStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.truth0.Truth;

import javax.tools.JavaFileObject;
import java.io.PrintStream;

@RunWith(JUnit4.class)
public class MementoProcessorTest {

    private static final String NATIVE_MEMENTO = Joiner.on("\n").join(
            "package com.test;",
            "",
            "import android.app.Fragment;",
            "import android.app.Activity;",
            "",
            "public final class RetainedActivity$Memento extends Fragment",
            "        implements com.github.mttkay.memento.MementoMethods {",
            "",
            "    String retainedString;",
            "    android.os.AsyncTask asyncTask;",
            "",
            "    public RetainedActivity$Memento() {",
            "        setRetainInstance(true);",
            "    }",
            "",
            "    @Override",
            "    public void retain(Activity source) {",
            "        RetainedActivity activity = (RetainedActivity) source;",
            "        this.retainedString = activity.retainedString;",
            "        this.asyncTask = activity.asyncTask;",
            "    }",
            "",
            "    @Override",
            "    public void restore(Activity target) {",
            "        RetainedActivity activity = (RetainedActivity) target;",
            "        activity.retainedString = this.retainedString;",
            "        activity.asyncTask = this.asyncTask;",
            "    }",
            "}");

    private static final String SUPPORTV4_MEMENTO = NATIVE_MEMENTO
            .replaceAll("android.app.Fragment", "android.support.v4.app.Fragment")
            .replaceAll("RetainedActivity", "RetainedFragmentActivity");

    private static final String CUSTOM_BASE_MEMENTO = NATIVE_MEMENTO
            .replaceAll("RetainedActivity", "RetainedActivityWithCustomBase");

    @Before
    public void dontPrintExceptions() {
        // get rid of the stack trace prints for expected exceptions
        System.setErr(new PrintStream(new NullStream()));
    }

    @Test
    public void itGeneratesMementoForNativeFragmentActivities() {
        JavaFileObject expectedSource = forSourceString("RetainedActivity$Memento", NATIVE_MEMENTO);
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("com/test/RetainedActivity.java"))
                .processedWith(new MementoProcessor())
                .compilesWithoutError()
                .and().generatesSources(expectedSource);
    }

    @Test
    public void itGeneratesMementoForSupportPackageFragmentActivities() {
        JavaFileObject expectedSource = forSourceString("RetainedFragmentActivity$Memento", SUPPORTV4_MEMENTO);
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("com/test/RetainedFragmentActivity.java"))
                .processedWith(new MementoProcessor())
                .compilesWithoutError()
                .and().generatesSources(expectedSource);
    }

    @Test
    public void itGeneratesMementoForActivitiesWithCustomBaseTypes() {
        JavaFileObject expectedSource = forSourceString("RetainedActivityWithCustomBase$Memento", CUSTOM_BASE_MEMENTO);
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("com/test/RetainedActivityWithCustomBase.java"))
                .processedWith(new MementoProcessor())
                .compilesWithoutError()
                .and().generatesSources(expectedSource);
    }

    @Test(expected = CompilationFailureException.class)
    public void itThrowsExceptionWhenRetainedFieldIsPrivate() {
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("com/test/RetainedActivityWithPrivateFields.java"))
                .processedWith(new MementoProcessor())
                .failsToCompile();
    }

    @Test(expected = CompilationFailureException.class)
    public void itThrowsExceptionWhenEnclosingTypeIsNotAnActivity() {
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("com/test/NotAnActivity.java"))
                .processedWith(new MementoProcessor())
                .failsToCompile();
    }
}
