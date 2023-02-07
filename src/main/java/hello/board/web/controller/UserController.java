package hello.board.web.controller;

import hello.board.domain.User;
import hello.board.web.DTO.UserDto;
import hello.board.web.DTO.UserLoginDto;
import hello.board.web.auth.LoginService;
import hello.board.web.constant.BasicConstant;
import hello.board.web.constant.SessionConstant;
import hello.board.web.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    // 회원가입 폼
    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "joinForm";
    }

    // 회원가입
    @PostMapping("/join")
    public String join(HttpServletRequest request, @Validated @ModelAttribute("user") UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("join exception {}",bindingResult);
            return "joinForm";
        }

        String dtoLogin_id = userDto.getLogin_id();
        String dtoPassword = userDto.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(dtoPassword);

        log.info("join encode password = " + encPassword);

        User user = User.builder()
                .login_id(dtoLogin_id)
                .password(encPassword)
                .build();

        log.info("login_id = " + dtoLogin_id);
        log.info("password = " + encPassword);

        User savedUser = userService.userSave(user);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConstant.LOGIN_ID, dtoLogin_id);

        return "redirect:/";
    }

    // 로그아웃
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

    // 로그인
    @PostMapping("/login")
    public String login(HttpServletRequest request, @RequestParam(value = BasicConstant.REQUEST_URI, required = false) String originalURI, @Validated @ModelAttribute("user") UserLoginDto userLoginDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("login exception {}",bindingResult);
            return "loginForm";
        }

        //String encPassword = bCryptPasswordEncoder.encode(userDto.getPassword());

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


        // 세션 처리 ( 공통 처리기 만들기 )
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
    public String loginIdDuplicateCheck(@RequestBody String loginId) {

        log.info("login Id : [{}] ", loginId);

        boolean result = userService.loginIdDuplicateCheck(loginId);
        if (result) {
            return "1";
        } else {
            return "0";
        }
    }
}
