package kr.poturns.virtualpalace.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Myungjin Kim on 2015-08-16.
 * <p>
 * Unity에서 사용될 클래스 또는 메소드를 나타내는 Annotation
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface UnityApi {
}
