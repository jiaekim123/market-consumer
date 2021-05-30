package com.pmc.market.service;

import com.pmc.market.entity.Status;
import com.pmc.market.error.exception.BusinessException;
import com.pmc.market.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailSendService {
    int size;

    private final JavaMailSender mailSender;

    private final Environment env;


    // 인증키 생성
    private String getKey(int size) {
        this.size = size;
        return getAuthCode();
    }

    // 인증 코드 난수 발생
    private String getAuthCode(){
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        int num = 0;

        while(stringBuffer.length() < size){
            num = random.nextInt(10);
            stringBuffer.append(num);
        }
        return stringBuffer.toString();
    }

    // 인증 메일 보내기
    public String sendAuthMail(String email){
        // 6자리 난수 인증번호 생성
        String authKey = getKey(6);
        // 인증메일 보내기
        sendAuthLinkTemplate(authKey, email);
//        sendAuthCodeTemplate(authKey, email);
        return authKey;
    }

    private void sendAuthLinkTemplate(String authKey, String email){
        // 인증메일 보내기
        try {
            MailUtil sendMail = new MailUtil(mailSender);
            sendMail.setSubject("회원가입 이메일 인증");
            sendMail.setText(new StringBuffer().append("<html><h1> [이메일 인증] </h1>")
                    .append("<p> 아래 링크를클릭하시면 이메일 인증이 완료됩니다.</p>")
                    .append("<a href='")
                    .append(env.getProperty("app.host"))
                    .append("/users/sign-up-confirm?email=")
                    .append(email)
                    .append("&status=")
                    .append(Status.ACTIVE)
                    .append("&auth=")
                    .append(authKey)
                    .append("' target='_blenk'>이메일 인증확인</a></html>")
                    .toString());

            sendMail.setFrom(env.getProperty("email.admin.account"),"관리자");
            sendMail.setTo(email);
            sendMail.send();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    private void sendAuthCodeTemplate(String authKey, String email){
        // 인증메일 보내기
        try {
            MailUtil sendMail = new MailUtil(mailSender);
            sendMail.setSubject("회원가입 이메일 인증");
            sendMail.setText(new StringBuffer().append("<html><h1> [이메일 인증] </h1>")
                    .append("<p> 아래 코드를 입력하시면 이메일 인증이 완료됩니다.</p>")
                    .append("<p>")
                    .append(authKey)
                    .append("</p></html>")
                    .toString());

            sendMail.setFrom("email.admin.account","관리자");
            sendMail.setTo(email);
            sendMail.send();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }
}

