# Thread 활용하기

### 자바에서 Thread만들기

- Thread 클래스를 상속하여 만들기
    ```
    private static final class ExtendedThread extends Thread {

        private String message;
        
        public ExtendedThread(final String message) {
             this.message = message;
        }
        
        @Override
        public void run() {
            log.info(message);
        }
    }
  
    Thread thread = new ExtendedThread("hello thread");
  ```

- Runnable 인터페이스 사용하기
    ```
    private static final class RunnableThread implements Runnable {
  
        private String message;
  
        public RunnableThread(final String message) {
            this.message = message;
        }
  
        @Override
        public void run() {
            log.info(message);
        }

    }

    Thread thread = new Thread(new RunnableThread("hello thread"));

    ```

### Thread Pool

- Fixed Thread Pool
    - 항상 지정된 수의 스레드가 실행 중
    - 스레드를 매번 생성하지 않으므로 응답이 매우 빠르다.
    - 모든 스레드가 사용 중이라면 새 작업은 대기열에 넣는다.
    - 실행시간을 예측할 수 없는 작업에 적합하다.
- Cached Thread Pool
    - 이전에 생성된 스레드가 사용가능하다면 재사용한다.
    - 사용 가능한 스레드가 없다면 새롭게 생성한다.
    - 합리적인 개수를 가지는 단기 작업에 유리하다.
    - IO등 실행시간을 예측할 수 없는 경우 적합하지 않다.

### accept-count

- request queue의 길이
- request 요청시 실행 가능한 스레드가 존재하지 않으면 대기큐에 메시지 형태로 들어가게 된다. 이 대기큐의 개수를 지정하는 것!
- 기본값은 100이다

### max-connections

- 서버가 허용할 수 있는 최대 커넥션 수
- 최대 커넥션 수에 도달하면 해당 요청은 ( 메시지는 ) 큐에 들어간다.
- Connector 방식에 따라 기본값이 다르다.
    - BIO ( Blocking IO )
        - connection 하나에 thread 하나 ( 톰캣 9부터 지원되지 않음 )
        - threads.max와 같은 값.
    - NIO ( Non-Blocking IO )
        - connection에 thread를 바로 할당하는 것이 아니라, 필요할 때에만 할당 ( 톰캣 6에서 추가 )
        - 10000
    - APR
        - 성능 개선을 위해 java가 아닌 Native 언어로 작성 ( 톰캣 5.5에서 추가되었으나 기본으로 함께 설치되어있지는 않음. 별도 설치 )
        - 8192

### threads.max

- 톰캣 내의 쓰레드 수 ( 그 순간 처리 가능한 트랜잭션의 수 )
- 너무 많이 설정한다면 스레드간의 문맥교환으로 성능이 저하될 수 있다.
- 너무 적게 설정한다면 사용되지 않는 CPU가 낭비된다.
- 기본값은 200이다.
