package com.aqua.services.java8;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase4;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class TestJava8 extends SystemTestCase4 {

    @Test
    @TestProperties(name = "Test Java8 lambda expression support")
    public void testLambda() {
        List<String> names = new ArrayList<>();
        names.add("foo");
        names.add("bar");
        names.add("zoo");
        report.report("Before changes " + names);
        List<String> changedNames = names.stream()
                .filter(n -> !n.equals("bar"))
                .map(n -> n.toUpperCase(Locale.ROOT))
                .collect(Collectors.toList());

        report.report("After changes " + changedNames);
        assertTrue(changedNames.size() == 2);
        assertTrue(changedNames.get(0).equals("FOO"));
    }

    @Test
    @TestProperties(name = "Test Java8 optional feature support")
    public void testOptional(){
        String name = "JSystem";
        Optional<String> opt = Optional.of(name);
        assertTrue(opt.isPresent());
    }

}
