package com.internshiptracker;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final SecureRandom random = new SecureRandom();

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) throws SQLException{
        userService.register(request.getUsername(), request.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpSession session,
                                      HttpServletRequest httpRequest)
            throws SQLException {

        User user = userService.login(request.getUsername(),request.getPassword());
        httpRequest.changeSessionId();
        session.setAttribute("userId", user.getId());
        session.setAttribute("csrfToken", generateToken());
        logger.info("User authenticated successfully");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiResponse(
            responseCode = "204",
            description = "Session invalidated successfully"
    )

    @Parameter(
            name = "X-CSRF-Token",
            description = "CSRF token obtained from GET /auth/csrf after login",
            required = true,
            in = ParameterIn.HEADER
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            session.invalidate();
            logger.info("User session invalidated successfully");
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/csrf")
    public ResponseEntity<String> getCsrfToken(HttpServletRequest serverRequest){
        HttpSession session = serverRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = (String) session.getAttribute("csrfToken");
        if(token == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }


    private String generateToken(){
        byte [] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        String encode = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return encode;
    }
}
