package cn.donting.web.os.core.exception;

import java.lang.reflect.Field;

public class CheckException extends Exception {
    private Field field;

    public CheckException(Field field) {
        super(field.getName()+" is null");
        this.field = field;
    }
    public CheckException(Field field,String msg) {
        super(field.getName()+msg);
        this.field = field;
    }

    public Field getField() {
        return field;
    }
}
