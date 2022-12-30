package hello.board.web.DTO;

import lombok.Getter;

@Getter
public enum BoardConditionEnum {
    loginId("아이디"), title("제목"), content("내용");

    private final String text;

    BoardConditionEnum(String text) {
        this.text = text;
    }

    public String value() {
        return text;
    }

}