package com.arxit.geolocindoor.config.swagger;

import lombok.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Generated
@RestController
@ApiIgnore
@RequestMapping(value = "${server.api.path}")
public class SwaggerController {

    @GetMapping(value = "/swagger")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }
}
