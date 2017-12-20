package test.Unit1;


import com.fatiger.framework.rest.awares.SpringContextAwareImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wengjiayu
 * @date 16/12/2017
 * @E-mail wengjiayu521@163.com
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
//@RunWith(SpringJUnit4ClassRunner.class)

@SuppressWarnings("all")
@Import(value = {SpringContextAwareImpl.class})
public class FirstTest {

    @Autowired
    private SpringContextAwareImpl apiController;

    @Test
    public void serviceTest() {
//
//        List.of("1","2","3").stream().forEach(s -> s.toString());


        System.setProperty("spring.active.profiles", "dev");
        System.out.println("================" + apiController);


        System.out.println("===============" + System.getProperty("java.ext.dirs"));

    }
}
