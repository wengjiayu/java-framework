package condition;

import com.fatiger.framework.core.context.BaseProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;

public class SwaggerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !Arrays.asList("prod", "product", "production", "prd").contains(BaseProperties.getString("spring.profiles.active"));
    }

}
