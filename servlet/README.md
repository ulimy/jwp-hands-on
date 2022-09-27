## 서블릿

: 클라이언트의 요청을 처리하고 결과를 응답하는 Servlet 클래스의 구현 규칙을 지킨 자바 웹 프로그래밍 기술

- 특징
    - Java Thread를 이용하여 동작한다. 서블릿 컨테이너가 멀티 스레드로 여러개의 서블릿을 관리하는 것!
    - MVC 패턴에서는 컨트롤러로 이용될 수 있다.
    - HTTP로 요청 및 응답하기 위해 HttpServlet 클래스를 상속받는다.
- 생명주기
    - init()
        - 요청이 들어왔을 때 해당 서블릿이 메모리에 존재하지 않는다면 이를 호출하여 적재한다.
        - 처음 단 한번만 실행!
        - 실행 중 서블릿이 변경 될 경우 기존 서블릿을 파괴하고 새롭게 적재한다.
    - service()
        - 클라이언트의 요청에 따라 처리하여 응답을 담는다.
    - destroy()
        - 컨테이너가 서블릿을 종료시키기 위해 실행한다.
        - 마지막에 단 한번만 실행!
- 동작 과정

  ![Servlet](https://user-images.githubusercontent.com/18046394/190846603-c13ff138-164e-40b8-806d-4b6397ff059d.png)

    1. 요청을 받아 HttpServletRequest와 HttpServletRequest 객체 생성
    2. uri를 분석하여 어떤 서블릿이 해당 요청을 처리할 수 있는지 찾기
        - 과거에는 서블릿을 xml 파일로 관리했기 때문에 web.xml에서 찾았다.
        - Servlet 3.0부터는 @WebServlet을 이용하면 자동으로 서블릿으로써 관리된다.
    3. 찾은 서블릿의 service()메서드를 호출하여 처리 시작
    4. service() 메서드에는 doGet(), doPost() 등이 정의되어있어 GET, POST 여부에 따라 실행된다. ( 메서드에 따라 doHead(), doOptions() 등등 다 있음 )
    5. 실행이 완료되면 HttpServletResponse에 값을 담는다. ( call by reference로써 파라미터로 전달되었기 때문에 반환할 필요는 없다. )
    6. 응답이 끝나면 HttpServletRequest, HttpServletRequest를 소멸시킨다.

## 서블릿 컨테이너

: 서블릿을 관리하는 컨테이너

- 요청을 받아 응답할 수 있도록 웹서버와 소켓으로 통신한다.
- 대표적인 서블릿 컨테이너가 바로 톰캣이다!
- 서블릿 컨테이너는 서블릿을 멀티쓰레드로 관리하기 때문에 컨테이너에 공유변수를 두면 안된다!!! 쓰레드가 공유할 수 있기 때문..ㅠㅠ
- 역할
    - 웹서버와의 통신 지원
        - 소켓 생성, accept 등등.. 따라서 비즈니스 로직에만 집중 가능
    - 서블릿 생명주기 관리
        - 서블릿 클래스를 로딩하여 인스턴스화, 초기화, 요청에 따른 서블릿 메서드 호출 등등
        - 서블릿의 생명이 끝나면 GC를 진행하는 것 까지!
    - 멀티 쓰레드 지원 및 관리
        - 요청을 멀티쓰레드를 통해 관리해줌으로써 안정성을 걱정하지 않아도 됨!
    - 선언적인 보안 관리
        - 보안에 관련된 내용을 서블릿 혹은 자바 클래스가 아닌 서블릿 컨테이너에 선언함으로써 보안에 대한 수정이 생겨도 다시 컴파일 할 필요가 없다.

## 톰캣? Dispatcher Servlet? Spring MVC?

- 톰캣은 하나의 Servlet Container로써 앞서 살펴봤던 기능들을 해준다.
- 이때, Spring MVC가 등장하면게 생긴 것이 Dispatcher Servlet이다.
- Dispatcher Servlet 또한 톰캣이 관리하는 하나의 서블릿인데, Spring MVC 에서는 이 서블릿으로 모든 요청이 가도록 설정되어 있는 것이다.
- Dispatcher Servlet은 모든 요청을 받아 알맞은 서블릿으로 요청을 위임하는 역할을 한다.
- 만약, 톰캣에서 Spring MVC를 사용하지 않는다면 평범한 서블릿 컨테이너처럼 uri 매핑을 통해 서블릿을 선택해야한다! 다만 Spring MVC는 일단 무조건 Dispatcher Servlet으로 가는 것일
  뿐!!!

## Filter

: 서블릿 컨테이너가 서블릿을 실행시키기 전에 특별한 처리를 하기 위한 기술

- 따라서 각 서블릿 별로 실행되는 것이 아니라 그 앞단에서 실행되기 때문에 모든 요청에 대해 실행된다!
- 구현 방법

    ```
    @WebFilter("/*") // 필터로 등록
    public class CharacterEncodingFilter implements Filter {
    
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            // 어쩌구 저쩌구 필터 처리
    
            chain.doFilter(request, response); // 다음 필터 실행
        }
    }
    ```

    - Servlet과 같이 과거에는 web.xml에 등록하여 사용했지만, @WebFilter를 통해 필터를 등록할 수 있다.
- Filter Chain
    - 여러개의 필터가 모여 하나의 체인형태로 존재하는 것
    - 필터의 doFilter() 메소드에서는 이 Filter Chain을 인자로 받는다.
    - 필터 처리가 끝나면 인자로 받았던 chain의 chain.doFilter() 메서드를 호출함으로써 다음 체인의 필터가 실행될 수 있도록 한다. 이때, 조건문 등을 통해서 어떤 필터를 실행할지도 결정할 수
      있다.
