package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类, 实现公共字段填充处理
 */
@Aspect
@Component  // 交给spring管理
@Slf4j
public class AutoFillAspect {

	/**
	 * 指定切入点
	 * 使用execution缩小范围, 用annotation确定
	 */
	@Pointcut("@annotation(com.sky.annotation.AutoFill)")
	public void autoFillPointCut(){}

	/**
	 * 通知
	 * 前置通知
	 */
	@Before("autoFillPointCut()")
	public void autoFill(JoinPoint joinPoint) {
		log.info("公共字段字段填充...");
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();// 方法签名对象

		// 获取数据库操作类型: 获取注解 -> 获取注解的值
		AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
		OperationType operationType = annotation.value();

		// 获取被拦截方法的参数
		Object[] args = joinPoint.getArgs();
		if (args == null || args.length == 0) {
			return;
		}
		Object entity = args[0];

		// 要赋值的数据
		LocalDateTime now = LocalDateTime.now();
		Long currentId = BaseContext.getCurrentId();

		// 根据不同操作类型, 通过反射赋值
		if (operationType == OperationType.INSERT) {
			try {
				// 通过反射获取方法
				Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
				Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
				Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
				Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);  // 方法名, 参数

				// 赋值
				setCreateTime.invoke(entity, now);
				setCreateUser.invoke(entity, currentId);
				setUpdateTime.invoke(entity, now);
				setUpdateUser.invoke(entity, currentId);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (operationType == OperationType.UPDATE) {
				// 通过反射赋值
			try {
				Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
				Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

				setUpdateTime.invoke(entity, now);
				setUpdateUser.invoke(entity, currentId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
