package hello.board.web.controller;

import hello.board.utils.RedisUtil;
import hello.board.web.DTO.MailDto;
import hello.board.web.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final RedisUtil redisUtil;

    @PostMapping("/send")
    public String sendMail(@RequestBody MailDto mailDto) {
        String authNum = emailService.sendSimpleMessage(mailDto);
        redisUtil.setDateExpire(mailDto.getAddress(), authNum, 60 * 5L);
        return authNum;
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
