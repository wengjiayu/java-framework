package test.Unit1;


import com.fatiger.framework.core.awares.EnvironmentWrapper;
import com.fatiger.framework.core.awares.SpringContextWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * @author wengjiayu
 * @date 16/12/2017
 * @E-mail wengjiayu521@163.com
 */

//@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@RunWith(SpringJUnit4ClassRunner.class)

@SuppressWarnings("all")
@Import(value = {EnvironmentWrapper.class, SpringContextWrapper.class})
public class FirstTest {

    @Inject
    private SpringContextWrapper springContextWrapper;

    @Test
    public void serviceTest() {
//
//        List.of("1","2","3").stream().forEach(s -> s.toString());


        System.out.println("================" + springContextWrapper);

////
//        while (true) {
//
//
//            try {
//                Thread.currentThread().wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//
//        }

//        System.out.println("===============" + System.getProperty("java.ext.dirs"));

    }
}
