import java.util.List;

public class GenericsFailExample {
    public static void main(final String[] args) {
        final SomeThingSpecific someThingNoGenerics = new SomeThingSpecific();

        //  Note that the Type parameter to SomeThingGeneric is NOT referenced in getSomeList
        final SomeThingGeneric<Object> someThingGenericObject = new SomeThingGeneric<Object>();
        final SomeThingGeneric<?> someThingGenericWildcard    = new SomeThingGeneric<Object>();
        final SomeThingGeneric someThingGenericRaw            = new SomeThingGeneric<Object>();

        for (final String s : someThingNoGenerics.getSomeList()) { }        //  happy compiler

        for (final String s : someThingGenericObject.getSomeList()) { }     //  happy compiler

        for (final String s : someThingGenericWildcard.getSomeList()) { }   //  happy compiler

        for (final String s : someThingGenericRaw.getSomeList()) { }        //  SAD compiler.  WTF?
    }

    public static class SomeThingGeneric<T> {
        public List<String> getSomeList() {
            return null;
        }
    }

   public static class SomeThingSpecific {
        public List<String> getSomeList() {
            return null;
        }
    }
}