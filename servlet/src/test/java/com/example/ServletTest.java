package com.example;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ServletTest {

    @Test
    void testSharedCounter() throws Exception {
        // 톰캣 서버 시작
        final var tomcatStarter = TestHttpUtils.createTomcatStarter();
        tomcatStarter.start();

        // shared-counter 페이지를 3번 호출한다.
        final var PATH = "/shared-counter";
        TestHttpUtils.send(PATH);
        TestHttpUtils.send(PATH);
        final var response = TestHttpUtils.send(PATH);

        // 톰캣 서버 종료
        tomcatStarter.stop();

        assertThat(response.statusCode()).isEqualTo(200);

        // expected를 0이 아닌 올바른 값으로 바꿔보자.
        // 예상한 결과가 나왔는가? 왜 이런 결과가 나왔을까?
        // sharedCounter라는 변수가 공유되고 있다. 초기값은 0이고 ++하는 요청이 3번 들어갔기 때문!
        assertThat(Integer.parseInt(response.body())).isEqualTo(3);
    }

    @Test
    void testLocalCounter() throws Exception {
        // 톰캣 서버 시작
        final var tomcatStarter = TestHttpUtils.createTomcatStarter();
        tomcatStarter.start();

        // local-counter 페이지를 3번 호출한다.
        final var PATH = "/local-counter";
        TestHttpUtils.send(PATH);
        TestHttpUtils.send(PATH);
        final var response = TestHttpUtils.send(PATH);

        // 톰캣 서버 종료
        tomcatStarter.stop();

        assertThat(response.statusCode()).isEqualTo(200);

        // expected를 0이 아닌 올바른 값으로 바꿔보자.
        // 예상한 결과가 나왔는가? 왜 이런 결과가 나왔을까?
        // localCounter는 로컬변수로써 0으로 초기화 되어있고 ++되어 반환했으므로 1
        assertThat(Integer.parseInt(response.body())).isEqualTo(1);
    }
}
