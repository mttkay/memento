package com.github.mttkay.memento;

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

    private static final JavaFileObject EXPECTED_MEMENTO =
            JavaFileObjects.forSourceString("RetainedActivity$Memento", Joiner.on("").join(
            "package com.test;",
            "",
            "import android.support.v4.app.Fragment;",
            "import android.support.v4.app.FragmentActivity;",
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
            "    public void restore(FragmentActivity source) {",
            "        RetainedActivity activity = (RetainedActivity) source;",
            "        this.retainedString = activity.retainedString;",
            "        this.asyncTask = activity.asyncTask;",
            "    }",
            "",
            "    @Override",
            "    public void retain(FragmentActivity target) {",
            "        RetainedActivity activity = (RetainedActivity) target;",
            "        activity.retainedString = this.retainedString;",
            "        activity.asyncTask = this.asyncTask;",
            "    }",
            "}"));

    @Before
    public void dontPrintExceptions() {
        // get rid of the stack trace prints for expected exceptions
        System.setErr(new PrintStream(new NullStream()));
    }

    @Test
    public void itGeneratesMementoFragmentClass() {
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("com/test/RetainedActivity.java"))
                .processedWith(new MementoProcessor())
                .compilesWithoutError()
                .and().generatesSources(EXPECTED_MEMENTO);
    }

    @Test(expected = CompilationFailureException.class)
    public void itThrowsExceptionWhenRetainedFieldIsPrivate() {
        Truth.ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("com/test/RetainedActivityWithPrivateFields.java"))
                .processedWith(new MementoProcessor())
                .failsToCompile();
    }
}
