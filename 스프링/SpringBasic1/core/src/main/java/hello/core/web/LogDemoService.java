package hello.core.web;


import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogDemoService {

//    private final ObjectProvider<MyLogger> myLoggerProvider;   //두 번째 방법

    private final MyLogger myLogger;   //세 번째 방법


    public void logic(String id) {
//        MyLogger myLogger = myLoggerProvider.getObject();    //두 번째 방법
        myLogger.log("service id = " + id);
    }
}
