package pers.clare;

import pers.clare.core.sqlquery.SQLStoreFactory;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws Exception {
        int c = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20000; j++) {
                test();
            }
        }
    }

    public static void test(){
            SQLStoreFactory.build(AccessLog.class,true);
    }
}
