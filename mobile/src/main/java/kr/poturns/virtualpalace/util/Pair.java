package kr.poturns.virtualpalace.util;

/**
 * Created by YeonhoKim on 2015-07-07.
 */
public class Pair<Class1, Class2> {

    public Class1 Class1;

    public Class2 Class2;

    public Pair(Class1 class1, Class2 class2) {
        Class1 = class1;
        Class2 = class2;
    }

    @Override
    public int hashCode() {
        int class1 = (Class1 == null)? 0 : Class1.hashCode();
        int class2 = (Class2 == null)? 0 : Class2.hashCode();

        return class1 ^ class2;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Pair) {
            try {
                Pair<Class1, Class2> target = (Pair<Class1, Class2>) o;
                return hashCode() == target.hashCode();

            } catch (ClassCastException e) {  }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Pair ");
        builder.append("(")
                .append(Class1 != null? Class1.toString() : null)
                .append(", ")
                .append(Class2 != null? Class2.toString() : null)
                .append(") : ");
        builder.append(super.toString());

        return builder.toString();
    }
}
