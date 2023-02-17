package hello.board.web.service;

import hello.board.web.DTO.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private String authNum;

    @Value("${spring.mail.username}")
    private String from;

    public String sendSimpleMessage(MailDto mailDto) throws MailParseException, MailSendException, MailAuthenticationException {
        // 인증코드 생성
        getRandomNum();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(mailDto.getAddress());
        message.setSubject("board 본인인증 인증 코드");
        message.setText(authNum);
        javaMailSender.send(message);

        // 인증코드 반환
        return authNum;
    }

    private void getRandomNum() {
        String num = "";
        for (int i = 0; i < 6; i++) {
            num+=new Random().nextInt(9);
        }
        authNum = num;
    }
}
