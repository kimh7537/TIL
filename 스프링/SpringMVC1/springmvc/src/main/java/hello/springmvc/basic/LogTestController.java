package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController  //원래 Controller는 문자를 리턴할때 뷰 이름이 반환됨, RestController는 문자열 반화할때 그냥 String이 반환됨
public class LogTestController {

//    private final Logger log = LoggerFactory.getLogger(LogTestController.class); //getClass()
//    롬복으로 생략 가능
    
    
    @RequestMapping("/log-test")
    public String logTest(){
        String name = "Spring";

        System.out.println("name = " + name);

//        log.trace(" trace my log"+name);  이렇게 사용하면 안됨, 자바 언어는 문자를 먼저 더하고 메서드를 호출함
        
        log.trace("trace log={}", name);   //만약 debug라면 문자를 미리 더하지 않고 호출이 안되기 때문에 이거 쓰기
        log.debug("debug log={}", name);
        log.info(" info log={}", name);
        log.warn(" warn log={}", name);
        log.error("error log={}", name);

        return "ok";
    }

}
