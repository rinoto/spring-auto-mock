# spring-auto-mock 

[![Build Status](https://travis-ci.org/rinoto/spring-auto-mock.svg?branch=master)](https://travis-ci.org/rinoto/spring-auto-mock) [![Coverage Status](https://coveralls.io/repos/github/rinoto/spring-auto-mock/badge.svg?branch=master)](https://coveralls.io/github/rinoto/spring-auto-mock?branch=master)

It automatically injects mockito mocks into your spring services for dependencies that are not found on the classpath (i.e. that are not registered in Spring).

### Download

Add the test dependency to your project:

```xml
<dependency>
    <groupId>com.github.rinoto.spring</groupId>
    <artifactId>spring-auto-mock</artifactId>
    <version>0.3</version>
    <scope>test</scope>
</dependency>
```


### Usage

To use it, just register the <code>AutoMockRegistryPostProcessor</code> in your test. E.g.:
 
```java
@ContextConfiguration(classes = { AutoMockRegistryPostProcessor.class, RestOfClasses.class, ... })
@RunWith(SpringJUnit4ClassRunner.class)
public class YourTest {
  ...
}
```

The mocked service can also be injected in your tests. See a usage example in https://github.com/rinoto/spring-auto-mock/blob/master/src/test/java/sbg/rinoto/spring/mock/AutoMockRegistryProcessorTest.java


### Why not use instead Mockito's @InjectMock and @Mock?
You need spring-auto-mock only if your tests need to run with SpringJUnit4ClassRunner, and you have a lot of dependencies that you do not want to mock explicitly.
Otherwise, please use @InjectMock and @Mock, or just mock your dependencies yourself.

### Does it also work with XML configuration?
No.

### Design Note
Before using spring-auto-mock, take two minutes to think about your application's architecture: your design is probably not good if you really need to use spring-auto-mock.

### Idea
Initially based on the work from Justin Ryan published [DZone](http://java.dzone.com/tips/automatically-inject-mocks)
