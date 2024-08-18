package org.springframework.aop;

import org.springframework.aop.framework.AdvisorChainFactory;
import org.springframework.aop.framework.DefaultAdvisorChainFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdvisedSupport {
    // 是否使用cglib代理
    private boolean proxyTargetClass = true;

    private TargetSource targetSource;

    private MethodMatcher methodMatcher;

    /**
     * 缓存，如果某个方法的拦截器已经取出，那么再次取的时候直接从缓存调取
     */
    private transient Map<Integer, List<Object>> methodCache;

    AdvisorChainFactory advisorChainFactory = new DefaultAdvisorChainFactory();

    private List<Advisor> advisors = new ArrayList<>();


    public AdvisedSupport(){
        this.methodCache = new ConcurrentHashMap<>(32);
    }

    public boolean isProxyTargetClass(){
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass){
        this.proxyTargetClass = proxyTargetClass;
    }

    public void addAdvisor(Advisor advisor){
        advisors.add(advisor);
    }

    public List<Advisor> getAdvisors(){
        return advisors;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    /**
     * 用来返回方法的拦截器链
     * @param method 对应方法
     * @param targetClass 目标实类
     * @return 该方法的拦截器链
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method,Class<?> targetClass){
        Integer cacheKey = method.hashCode();
        // 先从缓存中寻找是否存在拦截器链
        List<Object> cached = this.methodCache.get(cacheKey);
        if (cached == null){
            cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(
                    this,method,targetClass);
            this.methodCache.put(cacheKey,cached);
        }
        return cached;
    }
}
