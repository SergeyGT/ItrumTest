package itrum_test.itrum;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class ItrumApplicationTests {

    @Test
    void shouldExposeMainMethod() throws NoSuchMethodException {
        Method mainMethod = ItrumApplication.class.getMethod("main", String[].class);

        assertThat(mainMethod).isNotNull();
    }
}
