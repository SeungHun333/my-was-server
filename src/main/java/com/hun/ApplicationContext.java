package com.hun;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private final Map<String, Object> beanStorage = new HashMap<>();
    
    private ApplicationContext() {
        initBeanStorage();
    }

    private static class InnerApplicationContext {
        private static final ApplicationContext INSTANCE = new ApplicationContext();
    }

    public static ApplicationContext getApplicationContext() {
        return InnerApplicationContext.INSTANCE;
    }

    public <T> T getBean(String beanName, Class<T> requiredType) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Object bean = beanStorage.get(beanName);
        if (bean == null) {
            bean = createBean(beanName);
            registerStorage(beanName, bean);
        }

        if (!requiredType.isInstance(bean)) {
            throw new IllegalArgumentException("this class is not available for bean");
        }

        return (T) bean;
    }

    private void registerStorage(String beanName, Object bean) {
        beanStorage.put(beanName, bean);
    }

    private Object createBean(String beanName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class clazz = Class.forName(beanName);
        return clazz.getDeclaredConstructor().newInstance();
    }

    private void initBeanStorage() {
        beanStorage.put("handler.UrlControllerHandlerMapping", new UrlControllerHandlerMapping());
        beanStorage.put("handler.SimpleControllerHandlerAdapter", new SimpleControllerHandlerAdapter());
        beanStorage.put("controller.HelloController", new HelloController());
        beanStorage.put("controller.TimeController", new TimeController());
    }


    public HandlerMapping getHandlerMapping() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return getBean("handler.UrlControllerHandlerMapping", HandlerMapping.class);
    }

    public HandlerAdapter getHandlerAdapter() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return getBean("handler.SimpleControllerHandlerAdapter", HandlerAdapter.class);
    }
}
