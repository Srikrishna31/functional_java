package com.util;

import org.junit.Test;

public class ListTest {
    @Test
    public void testList() {

        var list = List.list(1, 2, 3, 4);

        System.out.println(list);

        var list1 = List.<Integer>list();

        System.out.println(list1.dropWhile(x -> false));
    }
}
