package com.systelab.seed.infrastructure.handlers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class HttpErrorHandler implements ErrorController {
    /**
     * @apiNote: Although this method is marked as deprecated it needs to be implemented for now in order to be able
     * to replace the spring BasicErrorController and allow the error configuration handler to forward errors to index.html page.
     *
     * @deprecated
     */
    @Override
    public String getErrorPath() {
        return "";
    }
}
