package jackrabbit.jackrabbit.core;

import org.apache.jackrabbit.core.query.lucene.join.QueryEngine;
import org.apache.jackrabbit.performance.AbstractPerformanceTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by thomas on 15.12.13.
 */
public class PerformanceTest extends AbstractPerformanceTest {

    @Test
    @Ignore
    public void testPerformance() throws Exception {

        System.setProperty(QueryEngine.NATIVE_SORT_SYSTEM_PROPERTY, "true");
        testPerformance("derby", this.getClass().getResourceAsStream("/jackrabbit-derby.xml"));
        testPerformance("orient", this.getClass().getResourceAsStream("/jackrabbit-orient.xml"));
        System.setProperty(QueryEngine.NATIVE_SORT_SYSTEM_PROPERTY, "false");
    }

    protected InputStream getDefaultConfig() {
        return this.getClass().getResourceAsStream("/jackrabbit-orient.xml");
    }
}
