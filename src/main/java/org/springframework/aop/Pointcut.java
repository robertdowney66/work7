package org.springframework.aop;


/**
 * 切点抽象
 * ClassFilter与MethodMatcher分别用于在不同级别上限定JoinPoint的匹配范围，满足不同需求的匹配
 * ClassFilter限定在类级别上，MethodMatcher限定在方法级别上
 */
public interface Pointcut {

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
