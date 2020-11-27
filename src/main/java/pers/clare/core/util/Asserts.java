package pers.clare.core.util;

public interface Asserts {
    static void notNull(Object value , String message,Object ...args){
        if(value==null){
         throw new RuntimeException(String.format(message,args));
        }
    }

}
