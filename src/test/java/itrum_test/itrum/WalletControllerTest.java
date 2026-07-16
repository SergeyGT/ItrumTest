package itrum_test.itrum;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PostMapping;

import itrum_test.itrum.API.WalletController;
import itrum_test.itrum.DTOs.WalletOperationRequest;

class WalletControllerTest {

    @Test
    void shouldExposePostMappingOnWalletsPath() throws NoSuchMethodException {
        Method method = WalletController.class.getMethod("operate", WalletOperationRequest.class);
        PostMapping annotation = method.getAnnotation(PostMapping.class);

        assertTrue(Arrays.asList(annotation.value()).contains("/wallets"));
    }
}
