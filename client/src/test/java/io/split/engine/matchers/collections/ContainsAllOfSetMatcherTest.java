package io.split.engine.matchers.collections;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by adilaijaz on 4/18/17.
 */
public class ContainsAllOfSetMatcherTest {
    @Test
    public void works_for_sets() {
        Set<String> set = new HashSet<>();
        set.add("first");
        set.add("second");

        ContainsAllOfSetMatcher matcher = new ContainsAllOfSetMatcher(set);

        assertThat(matcher.match(null), is(false));

        Set<String> argument = new HashSet<>();
        assertThat(matcher.match(argument), is(false));

        argument.add("second");
        assertThat(matcher.match(argument), is(false));

        argument.add("first");
        assertThat(matcher.match(argument), is(true));

        argument.add("third");
        assertThat(matcher.match(argument), is(true));
    }

    @Test
    public void works_for_lists() {
        List<String> list = new ArrayList<>();
        list.add("first");
        list.add("second");

        ContainsAllOfSetMatcher matcher = new ContainsAllOfSetMatcher(list);

        assertThat(matcher.match(null), is(false));

        List<String> argument = new ArrayList<>();
        assertThat(matcher.match(argument), is(false));

        argument.add("second");
        assertThat(matcher.match(argument), is(false));

        argument.add("first");
        assertThat(matcher.match(argument), is(true));

        argument.add("third");
        assertThat(matcher.match(argument), is(true));
    }

    @Test
    public void works_for_empty_paramter() {
        List<String> list = new ArrayList<>();

        ContainsAllOfSetMatcher matcher = new ContainsAllOfSetMatcher(list);

        assertThat(matcher.match(null), is(false));

        List<String> argument = new ArrayList<>();
        assertThat(matcher.match(argument), is(false));

        argument.add("second");
        assertThat(matcher.match(argument), is(false));

        argument.add("first");
        assertThat(matcher.match(argument), is(false));

        argument.add("third");
        assertThat(matcher.match(argument), is(false));
    }
}
