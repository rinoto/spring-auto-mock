package sbg.rinoto.spring.mock;

import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;

/**
 */
@SuppressWarnings("rawtypes")
public class MockFactoryBean implements FactoryBean {

    private Class type;

    public void setType(final Class type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject() throws Exception {
        return Mockito.mock(type);
    }

    @Override
    public Class getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
