package standardNaast.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
    @GetMapping({"/login", "/members", "/members/{id:[0-9]+}", "/seasons", "/teams",
            "/matches", "/abonnements", "/cotisations"})
    public String index() {
        return "forward:/index.html";
    }
}
