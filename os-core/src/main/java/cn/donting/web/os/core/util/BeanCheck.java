package cn.donting.web.os.core.util;

import cn.donting.web.os.api.annotation.NonNull;
import cn.donting.web.os.core.exception.CheckException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * BeanCheck 属性检查器
 * @see NonNull
 */
@Slf4j
public class BeanCheck {

    /**
     * 检查 {@link NonNull}  修饰的 字段，是否为null
     * @param obj
     * @throws CheckException
     */
    public static void checkNonNull(Object obj) throws CheckException {
        Class<?> aClass = obj.getClass();
        while (true){
            if(aClass.equals(Object.class)){
                return;
            }
            checkNonNull(obj,aClass);
            aClass=aClass.getSuperclass();
        }
    }
    private static void checkNonNull(Object obj,Class aclass) throws CheckException {
        Field[] fields =aclass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            NonNull annotation = field.getAnnotation(NonNull.class);
            if(annotation!=null){
                try {
                    if (field.get(obj) == null) {
                        throw new CheckException(field," is null");
                    }
                }catch (CheckException ex){
                    throw ex;
                }
                catch (Exception ex){
                    log.warn(ex.getMessage());
                }
            }
        }
    }



}
