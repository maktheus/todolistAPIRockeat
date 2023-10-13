package br.com.uchoa.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {

    public static void copyNonNullProperties(Object src, Object target){
        org.springframework.beans.BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }
    
    public static  String[] getNullPropertyNames(Object source){
        final BeanWrapper src = new BeanWrapperImpl(source);
        
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        java.util.Set<String> emptyNames = new HashSet<>();

        for(PropertyDescriptor pd : pds){
            Object srcValue = src.getPropertyValue(pd.getName());
            if(srcValue == null) emptyNames.add(pd.getName());

        }
        
        return emptyNames.toArray(new String[emptyNames.size()]);

    }
}
