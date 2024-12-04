//package org.example.configuration;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.hovita.csr.generator.service.common.aop.ErrorCode;
//import org.hovita.csr.generator.service.common.dto.BaseResponseDto;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class AuthEntryPointJwt implements AuthenticationEntryPoint {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
//            throws IOException, ServletException {
//        logger.error("Unauthorized error: {}", authException.getMessage());
//
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//        BaseResponseDto baseResponse = new BaseResponseDto();
//        baseResponse.setMessage("شما احراز هویت نشده اید.");
//        baseResponse.setErrorCode(ErrorCode.UNAUTHORIZED);
//        ObjectMapper mapper = new ObjectMapper();
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(mapper.writeValueAsString(baseResponse));
//
//    }
//
//}
//
