package reflections;

import org.apache.commons.collections4.map.SingletonMap;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Reflections {

    Class<?> aClass;
    Object classObject;
    Method method;
    HashMap<String, SingletonMap<Class<?>, Object>> classHashMap = new HashMap<>();

    public void executeStep(String className, String methodName, String... parameters) {

        try {

            if (!classHashMap.containsKey(className)) {
                aClass = Class.forName("reflections." + className);
                classObject = aClass.newInstance();
                SingletonMap<Class<?>, Object> singletonMap = new SingletonMap<>(aClass, classObject);
                classHashMap.put(className, singletonMap);
            } else {
                aClass = classHashMap.get(className).getKey();
                classObject = classHashMap.get(className).getValue();
            }

            if (parameters.length > 0) {
                executeClassMethodWithParameters(aClass, methodName, parameters);
            } else {
                executeClassMethodWithoutParameters(aClass, methodName);
            }

        } catch (Exception exception) {
            System.out.println("Exception Occurred: " + exception.getMessage() + " at: ");
            exception.printStackTrace();
        }
    }

    private void executeClassMethodWithoutParameters(Class<?> classObject, String methodName) throws Exception {
        method = classObject.getMethod(methodName);
        method.invoke(this.classObject);
    }

    private void executeClassMethodWithParameters(Class<?> classObject, String methodName, String... parameters) throws Exception {
        method = classObject.getMethod(methodName, parameters.getClass());
        method.invoke(this.classObject, (Object) parameters);
    }
}