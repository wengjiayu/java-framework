package test.unit2;

import java.util.function.Consumer;

/**
 * @author wengjiayu
 * @date 28/12/2017
 * @E-mail wengjiayu521@163.com
 */
public class TestOne implements Consumer {


    public String getObject(Consumer<String> c) {

        c.accept("dfdf");
        c.andThen(c);


        return null;
    }


    public static void main(String[] a) {

        new TestOne().getObject(t -> System.out.println(String.format("  ni hao   %s", t)));

    }


    @Override
    public void accept(Object o) {

    }
}
