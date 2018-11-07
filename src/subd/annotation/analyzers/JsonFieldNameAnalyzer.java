package subd.annotation.analyzers;

import org.json.JSONArray;
import org.json.JSONObject;
import subd.annotation.JsonFieldName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonFieldNameAnalyzer {

    public static JSONObject toJson(Object o) {
        JSONObject jsonObject = null;
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();

        try {
            jsonObject = new JSONObject();

            for (Field field : fields) {
                if (field.isAnnotationPresent(JsonFieldName.class)) {
                    JsonFieldName dbFieldName = field.getAnnotation(JsonFieldName.class);
                    String fieldName = dbFieldName.fieldName();

                    if (field.getType().isPrimitive() || field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                        jsonObject.put(fieldName, field.get(o));
                    } else if (field.getType().getSimpleName().equals("HashMap")) {
                        JSONObject object = new JSONObject();
                        HashMap<Object, Object> hashMap = (HashMap<Object, Object>) field.get(o);
                        for (Map.Entry<Object, Object> entry : hashMap.entrySet()) {
                            Object key = entry.getKey();
                            Object value = entry.getValue();
                            object.put(key.toString(), toJson(value));
                        }
                        jsonObject.put(fieldName, object);
                    } else if (field.getType().getSimpleName().equals("ArrayList")) {
                        JSONArray jsonArray = new JSONArray();
                        ArrayList<Object> arrayList = (ArrayList<Object>) field.get(o);
                        for (Object o1 : arrayList) {
                            jsonArray.put(toJson(o1));
                        }
                        jsonObject.put(fieldName, jsonArray);
                    } else {
                        jsonObject.put(fieldName, toJson(field.get(o)));
                    }
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            return jsonObject;
        } else {
            return new JSONObject();
        }
    }

}
