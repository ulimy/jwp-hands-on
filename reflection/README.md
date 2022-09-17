## Reflection

: 구체적인 클래스 타입을 알지 못해도 그 클래스의 메소드, 변수 등에 접근할 수 있도록 하는 자바 API

## 클래스 찾기

- `Class clazz = A.class;`
    - 타입으로 찾기
- `Class<?> clazz = Class.*forName*("A")**;**`
    - 이름으로 찾기

## 클래스 명 찾기

- `.getName()` : 패키지 경로 포함
- `.getSimpleName()` : 클래스명만!
- `.getCanonicalName()` : 전체 경로

## 생성자 찾기

- `Constructor constructor = clazz.getDeclaredConstructor();`
    - 기본 생성자를 찾는다.
    - 파라미터로 String.class 등의 타입을 넘기면 그 타입과 일치하는 생성자를 찾는다.
- `Constructor[] constructors = clazz.getDeclaredConstructors();`
    - private, public 등 클래스의 모든 생성자를 찾는다.
- `Constructor[] constructors = clazz.getConstructors();`
    - public 생성자만 찾는다.

## 메서드 찾기

- `Method method = clazz.getDeclaredMethods();`
    - 기본 메서드를 찾는다.
    - 파라미터로 “test”를 넘기면 test라는 이름의 메서드를 찾는다.
    - 파리미터로 String.class 등의 타입을 넘기면 그 타입과 일치하는 파라미터를 가지는 메서드를 찾는다. → 이때, 파라미터가 두개 이상이라면 클래스 배열을 만들어서 넘겨야 한다!
    - 존재하지 않는다면 NoSuchMethodException 예외가 발생한다.
- `Method[] methods = clazz.getDeclaredMethods();`
    - 모든 메서드를 찾는다.
- `Method methods[] = clazz.getMethods();`
    - public 메서드만 찾는다.
    - 상속받은 메서드까지 모두 찾아준다.
- `methods[0].invoke(new A());`
    - 해당 메서드를 실행시킨다.

## 필드 찾기

- `Field field = clazz.getField("이름");`
    - 특정 필드를 찾는다.
- `Field[] fields = clazz.getDeclaredFields();`
    - 모든 필드를 찾는다.
- `Field[] fields = clazz.getFields();`
    - public 필드만 찾는다.
    - 상속받은 필드까지 모두 찾아준다.
- `field.setAccessible(true);`
    - private한 필드를 접근할 수 있도록 변경한다.
- `fields[0].set(new A(), “” )`
    - set에 대상이 될 객체와 값을 넘김으로써 필드 값을 변경할 수 있다.

## 특정 어노테이션을 갖는 클래스 찾기

`Set<Class<?>> controllerTypes = reflections.getTypesAnnotatedWith(어노테이션명.class);`
