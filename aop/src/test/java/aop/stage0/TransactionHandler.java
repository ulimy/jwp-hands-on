package aop.stage0;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import aop.DataAccessException;
import aop.Transactional;

public class TransactionHandler implements InvocationHandler {

    private final PlatformTransactionManager transactionManager;
    private final Object target;

    public TransactionHandler(final PlatformTransactionManager transactionManager,
        final Object target) {
        this.transactionManager = transactionManager;
        this.target = target;
    }

    /**
     * @Transactional 어노테이션이 존재하는 메서드만 트랜잭션 기능을 적용하도록 만들어보자.
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        // 해당 파라미터를 가지는 메서드 찾아오기
        final var methods = target.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());

        // 어노테이션 붙어있다면 트랜잭션 적용
        if (methods.isAnnotationPresent(Transactional.class)) {
            return invokeWithTransaction(method, args);
        }

        // 어노테이션 붙어있지 않다면 그냥 실행
        return method.invoke(target, args);
    }

    private Object invokeWithTransaction(final Method method, final Object[] args) {
        final var transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            final var result = method.invoke(target, args);
            transactionManager.commit(transactionStatus);
            return result;
        } catch (InvocationTargetException | IllegalAccessException | RuntimeException e) {
            transactionManager.rollback(transactionStatus);
            throw new DataAccessException(e);
        }
    }
}
