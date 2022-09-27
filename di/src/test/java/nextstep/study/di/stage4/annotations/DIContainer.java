package nextstep.study.di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        this.beans = createBeans(classes);
        this.beans.forEach(this::setFields);
    }

    public static DIContainer createContainerForPackage(final String rootPackageName) {
        return new DIContainer(ClassPathScanner.getAllClassesInPackage(rootPackageName));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        Object bean = beans.stream()
            .filter(it -> aClass.isInstance(it))
            .findAny()
            .orElse(null);
        return aClass.cast(bean);
    }

    private Set<Object> createBeans(final Set<Class<?>> classes) {
        return classes.stream()
            .map(clazz -> createBean(clazz))
            .collect(Collectors.toSet());
    }

    private Object createBean(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException |
                 InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setFields(Object bean) {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            setField(bean, field);
        }
    }

    private void setField(Object bean, Field field) {
        try {
            field.setAccessible(true);
            if (field.get(bean) == null) {
                field.set(bean, getBean(field.getType()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
