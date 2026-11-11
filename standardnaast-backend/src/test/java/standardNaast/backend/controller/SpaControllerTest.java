package standardNaast.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SpaControllerTest {
    @Test
    void desktopSettingsSupportBrowserRefreshWithoutForwardingApiOrMissingAssets() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new SpaController()).build();
        mvc.perform(get("/settings")).andExpect(status().isOk()).andExpect(forwardedUrl("/index.html"));
        mvc.perform(get("/api/desktop/storage")).andExpect(status().isNotFound());
        mvc.perform(get("/missing.js")).andExpect(status().isNotFound());
    }
}
