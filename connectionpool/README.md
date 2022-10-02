## DriverManager

- 커넥션을 만드는 역할
- JDBC 4.0이전에는Class.forName메서드를 사용하여JDBC드라이버를 직접 등록해야 했지만 JDBC 4.0부터DriverManager가 적절한JDBC드라이버를 찾는다.

```
Connection connection = DriverManager.getConnection("디비주소");
```

## DataSource

- 데이터베이스, 파일 같은 물리적 데이터 소스에 연결을 위한 인터페이스
- 각 데이터베이스(h2, MySQL)가 구현체를 제공한다.
- DriverManager가 아닌 DataSource를 이용하면 애플리케이션 코드의 변경 없이 properties만 수정하면 된다.

```
final JdbcDataSource dataSource = new JdbcDataSource();
dataSource.setURL("디비주소");

Connection connection = dataSource.getConnection("username", "pw")
```

## 커넥션 풀링 ( Connection Pooling )

: DataSource를 통해 미리 커넥션을 만들어두는 것

- 새로운 커넥션을 생성하는 비용은 꽤 크기 때문에 미리 생성해놓고 재사용하면 성능상 이점을 가질 수 있다.

```
JdbcConnectionPool cp = JdbcConnectionPool.create("주소", "username", "pw");
Connection connection = cp.getConnection();
```

## HikariCP

- Spring boot 2.0부터 기본으로 재택된 DataSource
- 자바 코드를 통해 설정

    ```
    HikariConfig config = new HikariConfig();
    
    config.setJdbcUrl("주소"); // 필수
    config.setUsername("username"); // 필수
    config.setPassword("pw"); // 필수
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    
    HikariDataSource ds = new HikariDataSource(config);
    ```

- property file을 통해 설정

    ```
    dataSourceClassName=org.postgresql.ds.PGSimpleDataSource
    dataSource.user=test
    dataSource.password=test
    dataSource.databaseName=mydb
    dataSource.portNumber=5432
    dataSource.serverName=localhost
    ```
- 주요 설정
    - cachePrepStmts
        - 캐시 여부 설정
        - 기본값 false
        - 이 값이 false라면 캐시 자체를 하지 않기 때문에 캐시 관련 설정값을 입력해도 아무런 영향을 끼치지 않는다.
    - prepStmtCacheSize
        - 하나의 Connection에서 캐시 할 Prepared Statements의 수
        - 기본값 25
    - useServerPrepStmts
        - Prepared Statements 지원 여부
        - 기본값 false
    - prepStmtCacheSqlLimit
        - DataSource가 캐시 할 Prepared SQL의 최대 길이
        - 기본값 2048
- Connection 객체를 한번 wrapping한 `PoolEntry`라는 Type으로 Connection을관리하고, `ConcurrentBag`이라는 구조체에 모아둔다.

  → ConcurrentBag에서 사용 가능한 PoolEntry(Connection)을 꺼내는 것!

- Connection을 얻는 과정
    1. 이전에 커넥션을 연결한 적 있는 스레드라면 해당 커넥션을 할당할 수 있는지 확인
    2. 할당할 수 있다면 해당 커넥션을, 그렇지 않다면 사용가능한 다른 커넥션을 반환
    3. 사용가능한 커넥션이 없다면 Connection timeout에 설정된 시간만큼 기다린 후 Exception 발생

       → 기본 설정은 30초!


- Connection을 반납하는 과정
    1. 스레드는 할당된 커넥션을 사용한 후, connection.close() 메서드 호출
    2. Connection의 상태를  `STATE_NOT_IN_USE`로 변경함으로써 사용 가능한 커넥션으로 변경
    3. Connection을 얻기 위해 `handOffQueue`에 대기하고 있는 스레드가 있다면 바로 할당
    4. 해당 Connection에 방금 반납한 스레드의 정보를 등록함으로써 같은 스레드가 다시 요청을 보내면 해당 Connection이 반납될 수 있도록 함

- 적절한 Maximum Pool Size 고르기
    - 스레드 내에서 하나의 작업에 필요한 커넥션 수가 사용할 수 있는 커넥션 수보다 영원히 크다면 `Dead lock`에 빠질 수 있다!
    - 따라서 HikariCP에서 제안한 공식은 다음과 같다.

        ```
        Tn x (Cm - 1) + 1
        
        Tn : Thread의 최대 수
        Cm: 하나의 Task에서 필요한 Connection 갯수
        ```
