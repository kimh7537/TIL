package com.kimlog.api.controller;

import com.kimlog.api.request.PostCreate;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class PostController {

    //SSR -> JPS, thymeleaf, mustache, freemarker
    //SPQ ->
    //     vue -> vue + SSR = nuxt
    //     react -> react + SSR = next

    @PostMapping("/posts")
    public Map<String, String> get(@RequestBody @Valid PostCreate params, BindingResult result){
        //데이터를 검증하는 이유
        // 1.client 개발자가 실수로 값을 보내지 않는 경우
        // 2.client bug로 값이 누락될 수 있음
        // 3.외부에서 값을 임으로 조작해 보낼 수 있음
        // 4. DB에 값을 저장할 때 의도치 않은 오류가 발생할 수 있음
        // 5. 서버 개발자의 편안함을 위해

        log.info("params={}", params.toString());
        if(result.hasErrors()){
            List<FieldError> fieldErrors = result.getFieldErrors();
            FieldError firstFieldError = fieldErrors.get(0);
            String fieldName = firstFieldError.getField();//title
            String errorMessage = firstFieldError.getDefaultMessage();//..에러 메시지

            Map<String, String> error = new HashMap<>();
            error.put(fieldName, errorMessage);
            return error;
        }

        return Map.of();
    }


}
