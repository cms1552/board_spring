package hello.board.web.controller;

import hello.board.utils.RedisUtil;
import hello.board.web.DTO.MailDto;
import hello.board.web.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final RedisUtil redisUtil;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody MailDto mailDto) {
        try {
            String authNum = emailService.sendSimpleMessage(mailDto);
            redisUtil.setDateExpire(mailDto.getAddress(), authNum, 60 * 5L);
            return new ResponseEntity<>(authNum, HttpStatus.OK);
        } catch (MailParseException e) {
            return new ResponseEntity<>("잘못된 메일 주소입니다.", HttpStatus.BAD_REQUEST);
        } catch (MailAuthenticationException e) {
            return new ResponseEntity<>("메일 인증에 문제가 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MailSendException e) {
            return new ResponseEntity<>("메일 전송 실패입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/auth")
    public String authenticationCode(@RequestParam String address, @RequestParam String authCode) {
        String data = redisUtil.getData(address);
        if (authCode.equals(data)) {
            return "ok";
        } else {
            throw new IllegalStateException("인증코드가 맞지 않습니다.");
        }
    }
}
