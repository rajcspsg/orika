package ma.glasnost.orika.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Named;

public class ParanamerUtil {

    private static final ConcurrentHashMap<Class, String[]> paranamer = new ConcurrentHashMap<Class, String[]>();
    private static final Predicate<String[]> nonEmptyArrayPredicate = (String[] s) -> s!= null || s.length!=0;

    public static String[] getParanamers(Constructor constructor) {
        return paranamer.compute(constructor.getDeclaringClass(), (k, paranamerArr) -> paranamerArr!=null?paranamerArr:getByteCodeAnnotationParanamers(constructor));
    }

    private static String[] getByteCodeAnnotationParanamers(Constructor constructor) {
        return Optional.ofNullable(getByteCodeParanamers(constructor)).filter(nonEmptyArrayPredicate).
                orElseGet(() -> Optional.ofNullable(getAnnotationParanamers(constructor)).filter(nonEmptyArrayPredicate).orElseGet(() -> new String[] {}));
    }

    private static String[] getByteCodeParanamers(Constructor constructor) {
        Parameter[] parameters = constructor.getParameters();
        return Stream.of(parameters).map(p -> p.getName()).toArray(size -> new String[size]);
    }

    private static String[] getAnnotationParanamers(Constructor constructor) {
        Annotation[] annotations = constructor.getDeclaredAnnotations();
        return Stream.of(annotations).filter(a -> a instanceof  Named).map(ann -> ((Named) ann).value()).toArray(size -> new String[size]);
    }
}
