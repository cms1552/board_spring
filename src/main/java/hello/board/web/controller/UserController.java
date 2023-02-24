package hello.board.web.controller;

import hello.board.domain.User;
import hello.board.utils.RedisUtil;
import hello.board.web.DTO.LoginIdCheckDto;
import hello.board.web.DTO.UserDto;
import hello.board.web.DTO.UserLoginDto;
import hello.board.web.DTO.UserModifyDto;
import hello.board.web.annotation.Auth;
import hello.board.web.auth.LoginService;
import hello.board.web.constant.BasicConstant;
import hello.board.web.constant.SessionConstant;
import hello.board.web.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final LoginService loginService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RedisUtil redisUtil;

    // 회원가입 폼
    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "joinForm";
    }

    // 회원가입
    @PostMapping("/join")
    public String join(HttpServletRequest request, @Validated @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.info("join exception {}", bindingResult);
            model.addAttribute("bindingResult", bindingResult.getFieldError());
            return "joinForm";
        }

        // 인증코드 검사
        String data = redisUtil.getData(userDto.getMailAddress());
        if (data == null) {
            model.addAttribute("errMsg", "본인인증을 완료하세요!");
            return "joinForm";
        }
        log.info(data);
        if (!data.equals(userDto.getAuthCode())) {
            model.addAttribute("errMsg", "인증코드가 맞지 않습니다!");
            return "joinForm";
        }

        String dtoLogin_id = userDto.getLogin_id();
        String dtoPassword = userDto.getPassword();
        String dtoMailAddress = userDto.getMailAddress();
        String encPassword = bCryptPasswordEncoder.encode(dtoPassword);

        log.info("join encode password = " + encPassword);

        User user = User.builder()
                .login_id(dtoLogin_id)
                .password(encPassword)
                .mailAddress(dtoMailAddress)
                .build();

        log.info("login_id = " + dtoLogin_id);
        log.info("password = " + encPassword);

        User savedUser = userService.userSave(user);

        // 세션 로그인 서비스 : 세션 추가
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstant.LOGIN_ID, dtoLogin_id);

        return "redirect:/";
    }

    // 로그아웃
    @Auth
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        loginService.logout(request);
        return "index";
    }

    // 로그인 폼
    @GetMapping("/login")
    public String loginForm(Model model) {

        model.addAttribute("user", new UserLoginDto());
        return "loginForm";
    }

    // 세션 로그인
    @PostMapping("/login")
    public String login(HttpServletRequest request, @RequestParam(value = BasicConstant.REQUEST_URI, required = false) String originalURI, @Validated @ModelAttribute("user") UserLoginDto userLoginDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("login exception {}", bindingResult);
            return "loginForm";
        }

        User user = User.builder()
                .login_id(userLoginDto.getLoginId())
                .password(userLoginDto.getPassword())
                .build();

        try {
            loginService.login(user);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errMsg", e.getMessage());
            return "loginForm";
        } catch (IllegalStateException e) {
            model.addAttribute("errMsg", e.getMessage());
            return "loginForm";
        }


        // 세션 로그인 세션 처리 ( 공통 처리기 만들기 )
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstant.LOGIN_ID, user.getLoginId());

        // original request URI 가 존재할 경우 해당 경로로 리다이렉팅
        if (originalURI == null) {
            return "redirect:/";
        }
        return "redirect:" + originalURI;
    }

    // 아이디 중복 체크
    @PostMapping("/duplicateCheck")
    @ResponseBody
    public String loginIdDuplicateCheck(@Validated @RequestBody LoginIdCheckDto loginId, BindingResult bindingResult) {
        log.info("login Id : [{}] ", loginId);

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return bindingResult.getFieldError().getDefaultMessage();
        }

        boolean result = userService.loginIdDuplicateCheck(loginId);
        if (result) {
            return "1";
        } else {
            return "0";
        }
    }

    // 본인인증 코드 유효시간
    @GetMapping("/authcode/expire")
    @ResponseBody
    public Long getAuthCodeExpire(@RequestParam String key) {
        Long expire = redisUtil.getExpire(key);
        return expire;
    }

    // 회원 정보 수정
    @Auth
    @GetMapping("/modify")
    public String getUserModify(@SessionAttribute(name = SessionConstant.LOGIN_ID) String loginId, Model model) {
        UserModifyDto userModifyDto = new UserModifyDto();
        userModifyDto.toDto(userService.findByLoginId(loginId));
        model.addAttribute("userDto2", userModifyDto);
        model.addAttribute("userDto1", new UserModifyDto());
        return "userModify";
    }

    // 회원 정보 수정
    @Auth
    @PostMapping("/modify")
    public String modifyUser(@Validated @ModelAttribute UserModifyDto userModifyDto, BindingResult bindingResult, Model model) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getFieldError().toString());
            model.addAttribute("userDto1", new UserModifyDto());
            model.addAttribute("userDto2", userModifyDto);
            model.addAttribute("bindingResult", bindingResult.getFieldError());
            return "userModify";
        }

        // 비밀번호 확인 검증 ( password == passwordCheck )
        if (!userModifyDto.getPassword().equals(userModifyDto.getPasswordCheck())) {
            model.addAttribute("userDto1", new UserModifyDto());
            model.addAttribute("userDto2", userModifyDto);
            bindingResult.addError(new ObjectError("userModifyDto", "비밀번호가 맞지 않습니다."));
            model.addAttribute("bindingResult", bindingResult.getGlobalError());
            return "userModify";
        }

        /*
        * 수정 쿼리
        * */
        String encoded = bCryptPasswordEncoder.encode(userModifyDto.getPassword());
        userModifyDto.setPassword(encoded);
        UserModifyDto userModifyDto1 = userService.userUpdate(userModifyDto);

        model.addAttribute("userDto1", new UserModifyDto());
        model.addAttribute("userDto2", userModifyDto1);
        return "userModify";
    }
}
