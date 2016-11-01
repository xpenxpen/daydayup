package org.xpen.hello.spring.aop;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceExceptionTranslatorInterceptor implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionTranslatorInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            LOGGER.debug("HTTP invoker request START. ({}.{})",
                    invocation.getMethod().getDeclaringClass().getName(),
                    invocation.getMethod().getName());
            return invocation.proceed();
        } catch (Throwable e) {
            LOGGER.error("Exception occurred while access HTTP invoker request.", e);
            throw new RuntimeException("HTTP invoker request failed");
            //throw translateException(e);
        }
    }

    static RuntimeException translateException(Throwable e) {
        WrappedException serviceException = new WrappedException();

        try {
            serviceException.setStackTrace(e.getStackTrace());
            serviceException.setMessage(e.getClass().getName() +
                    ": " + e.getMessage());
            getField(Throwable.class, "detailMessage").set(serviceException, 
                    e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null) {
                getField(Throwable.class, "cause").set(serviceException,
                        translateException(cause));
            }
        } catch (IllegalArgumentException e1) {
            // Should never happen, ServiceException is an instance of Throwable
        } catch (IllegalAccessException e2) {
            // Should never happen, we've set the fields to accessible
        } catch (NoSuchFieldException e3) {
            // Should never happen, we know 'detailMessage' and 'cause' are
            // valid fields
        }
        return serviceException;
    }

    static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field f = clazz.getDeclaredField(fieldName);
        if (!f.isAccessible()) {
            f.setAccessible(true);
        }
        return f;
    }

}
