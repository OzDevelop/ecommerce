package fastcampus.ecommerce.batch.util;

import static java.lang.reflect.Modifier.isStatic;

import fastcampus.ecommerce.batch.domain.product.Product;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** class에서 필드 값을 뽑아내기 위한 util */
public class ReflectionUtils {
    public static List<String> getFieldNames(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field: fields) {
            if(!isStatic(field.getModifiers())) {
                fieldNames.add(field.getName());
            }
        }
        return fieldNames;
    }
}
