package hello.board.web.controller;

import hello.board.domain.User;
import hello.board.web.DTO.UserDto;
import hello.board.web.auth.LoginService;
import hello.board.web.constant.SessionConstant;
import hello.board.web.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final LoginService loginService;

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

        User user = User.builder()
                .login_id(dtoLogin_id)
                .password(dtoPassword)
                .build();

        log.info("login_id = " + dtoLogin_id);
        log.info("password = " + dtoPassword);

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
        model.addAttribute("user", new UserDto());
        return "loginForm";
    }

    // 로그인
    @PostMapping("/login")
    public String login(HttpServletRequest request, @Validated @ModelAttribute("user") UserDto userDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("login exception {}",bindingResult);
            return "loginForm";
        }

        User user = User.builder()
                .login_id(userDto.getLogin_id())
                .password(userDto.getPassword())
                .build();


        loginService.login(user);

        // 세션 처리 ( 공통 처리기 만들기 )
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstant.LOGIN_ID, user.getLoginId());

        return "redirect:/";
    }
}
