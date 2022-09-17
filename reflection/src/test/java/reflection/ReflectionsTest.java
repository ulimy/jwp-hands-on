package reflection;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotation.Controller;
import annotation.Repository;
import annotation.Service;

class ReflectionsTest {

    private static final Logger log = LoggerFactory.getLogger(ReflectionsTest.class);

    @Test
    void showAnnotationClass() throws Exception {
        Reflections reflections = new Reflections("examples");

        // TODO 클래스 레벨에 @Controller, @Service, @Repository 애노테이션이 설정되어 모든 클래스 찾아 로그로 출력한다.

        List<Class<? extends Annotation>> annotations = List.of(Controller.class, Service.class, Repository.class);

        for (Class annotation : annotations) {
            Set<Class> classes = reflections.getTypesAnnotatedWith(annotation);
            for (Class clazz : classes) {
                log.info(clazz.getName());
            }
        }

    }
}
