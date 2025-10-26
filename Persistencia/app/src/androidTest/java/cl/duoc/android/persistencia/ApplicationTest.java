package cl.duoc.android.persistencia;

import android.app.Application;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    @Test
    public void useAppContext() {
        // Pobierz kontekst aplikacji, która jest testowana.
        Context appContext = ApplicationProvider.getApplicationContext();

        // Przykład asercji: sprawdź, czy nazwa pakietu jest poprawna.
        assertEquals("cl.duoc.android.persistencia", appContext.getPackageName());
    }

    @Test
    public void application_isNotNull() {
        // Możesz również uzyskać instancję samej aplikacji.
        Application application = ApplicationProvider.getApplicationContext();
        assertNotNull(application);
    }
}