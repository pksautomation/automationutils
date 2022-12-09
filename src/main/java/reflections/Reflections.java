package reflections;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.CustomRuntimeException;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.cucumber.TestContext;
import org.apache.commons.collections4.map.SingletonMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Reflections {

    Class<?> aClass;
    Object classObject;
    HashMap<String, SingletonMap<Class<?>, Object>> classHashMap = new HashMap<>();

    private LoggerUtils loggerUtils;

    public Reflections(Config config) {
        this.loggerUtils = new LoggerUtils(config);
    }

    public void executeStep(String packageName, String className, String methodName,
                            TestContext testContext, String... parameters) throws CustomRuntimeException {
        if (!classHashMap.containsKey(className)) {
            try {
                aClass = Class.forName(packageName + "." + className);
            } catch (ClassNotFoundException e) {
                throw new CustomRuntimeException("Class: " + packageName + "." + className + " not found.");
            }
            try {
                if (methodName.equals("")) {

                    Constructor<?> constructor = aClass.getConstructor(TestContext.class);
                    classObject = constructor.newInstance(testContext);
                    loggerUtils.logComment("Instance using Constructor Created");
                } else
                    classObject = aClass.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new CustomRuntimeException
                        ("Exception while Calling or Finding Constructor with TextContext as parameter");
            }
            SingletonMap<Class<?>, Object> singletonMap = new SingletonMap<>(aClass, classObject);
            classHashMap.put(className, singletonMap);
        } else {
            aClass = classHashMap.get(className).getKey();
            classObject = classHashMap.get(className).getValue();
        }
        try {
            if (!methodName.equals("")) {
                if (parameters.length > 0)
                    aClass.getMethod(methodName, parameters.getClass())
                            .invoke(this.classObject, (Object) parameters);
                else
                    aClass.getMethod(methodName).invoke(this.classObject);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new CustomRuntimeException
                    ("Exception while Invoking or Finding Method: " + methodName);
        }
    }
}