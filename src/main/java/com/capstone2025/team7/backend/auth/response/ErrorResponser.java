package com.capstone2025.team7.backend.auth.response;

import com.capstone2025.team7.backend.utils.response.ErrorResponse;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

public class ErrorResponser {
    public static void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus) throws IOException {
        Gson gson = new Gson();
        ErrorResponse errorResponse = ErrorResponse.of(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        response.getWriter().write(gson.toJson(errorResponse, ErrorResponse.class));
    }
}
