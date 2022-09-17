package reflection;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class Junit4TestRunner {

    @Test
    void run() throws Exception {
        Class<Junit4Test> clazz = Junit4Test.class;

        // TODO Junit4Test에서 @MyTest 애노테이션이 있는 메소드 실행
        Junit4Test junit = clazz.getConstructor().newInstance();

        for (Method method : clazz.getMethods()) {
            if (method.getAnnotation(MyTest.class) == null) {
                continue;
            }
            method.invoke(junit);
        }
    }
}
