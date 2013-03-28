import static java.lang.reflect.Array.*;
import java.util.Arrays;

public class DeepConverter {
		
	public static void main(String args[]) {		
		long L1[][][] = {{{1,2},{3,4}}, {{5,6}}, {{7}},{{8,9,10,11}}};
		L1 = new long[2][0][7];
		Long L2[][] = (Long[][])wrap(L1);
		System.out.println(Arrays.deepToString(L2));		
	}
	
	public static Object wrap(Object src) {		
		try {
			int length = src.getClass().isArray() ? getLength(src) : 0;		
			if(length == 0)
				return src;		
			Object dest = newInstance(typeCastTo(wrap(get(src, 0))), length);		
			for(int i = 0; i < length; i++)
				set(dest, i, wrap(get(src, i)));		
			return dest;
		} catch(Exception e) {
			throw new ClassCastException("Object to wrap must be an array of primitives with no 0 dimensions");
		}
	}
		
	private static Class<?> typeCastTo(Object obj) {
		Class<?> type = obj.getClass();
		if(type.equals(boolean.class)) return Boolean.class;
	    if(type.equals(byte.class)) return Byte.class;
	    if(type.equals(char.class)) return Character.class;
	    if(type.equals(double.class)) return Double.class;
	    if(type.equals(float.class)) return Float.class;
	    if(type.equals(int.class)) return Integer.class;
	    if(type.equals(long.class)) return Long.class;
	    if(type.equals(short.class)) return Short.class;
	    if(type.equals(void.class)) return Void.class;	    
	    return type;
	}
}
