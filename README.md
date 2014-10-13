# spring-auto-mock 

It automatically injects mockito mocks into your spring services for dependencies that are not found on the classpath (i.e. that are not registered in Spring).

### Usage

To use it, just register the <code>AutoMockRegistryPostProcessor</code> in your test. E.g.:
 
```java
@ContextConfiguration(classes = { AutoMockRegistryPostProcessor.class, RestOfClasses.class, ... })
@RunWith(SpringJUnit4ClassRunner.class)
public class YourTest {
  ...
}
```

The mocked service can also be injected in your tests. See a usage example in (https://github.com/rinoto/spring-auto-mock/blob/master/src/test/java/sbg/rinoto/spring/mock/AutoMockRegistryProcessorTest.java)

### Idea
Initially based on the work from Justin Ryan published DZone (http://java.dzone.com/tips/automatically-inject-mocks)
