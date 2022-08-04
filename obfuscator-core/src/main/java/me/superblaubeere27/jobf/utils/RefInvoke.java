package me.superblaubeere27.jobf.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by uniking on 17-7-21.
 */

public class RefInvoke {
    public static  Object invokeStaticMethod(String class_name, String method_name, Class[] pareTyple, Object[] pareVaules){

        try {

            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getMethod(method_name,pareTyple);
            return method.invoke(null, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeStaticDeclaredMethod(String class_name, String method_name, Class[] pareTyple, Object[] pareVaules){

        try {

            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(null, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeStaticDeclaredMethod(Class obj_class, String method_name, Class[] pareTyple, Object[] pareVaules){

        try {
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(null, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeStaticDeclaredMethod(ClassLoader classLoader, String class_name, String method_name, Class[] pareTyple, Object[] pareVaules){

        try {

            Class obj_class = classLoader.loadClass(class_name);
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(null, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeMethod(String class_name, String method_name, Object obj ,Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getMethod(method_name,pareTyple);
            return method.invoke(obj, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeMethod(ClassLoader classLoader, String class_name, String method_name, Object obj ,Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = classLoader.loadClass(class_name);
            Method method = obj_class.getMethod(method_name,pareTyple);
            return method.invoke(obj, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }


    public static  Object invokeDeclaredMethod(String class_name, String method_name, Object obj ,Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(obj, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeConstructor(String class_name, Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = Class.forName(class_name);
            Constructor c = (Constructor) RefInvoke.invokeDeclaredMethod("java.lang.Class",
                    "getDeclaredConstructorInternal", obj_class, new Class[]{ Class[].class}, new Object[]{pareTyple});
            c.setAccessible(true);
            if(pareTyple.length == 0){
                return c.newInstance();
            }else{
                return c.newInstance(pareVaules);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeDeclaredMethod(ClassLoader classLoader, String class_name, String method_name, Object obj ,Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = classLoader.loadClass(class_name);
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(obj, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeDeclaredMethod(Class obj_class, String method_name, Object obj ,Class[] pareTyple, Object[] pareVaules){

        try {
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(obj, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeSingletonMethod(String class_name, String method_name, Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = Class.forName(class_name);
            Method getInstance = obj_class.getDeclaredMethod("getInstance",new Class[]{});
            getInstance.setAccessible(true);
            Object singleton = getInstance.invoke(null, new Object[]{});

            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(singleton, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static  Object invokeSingletonMethod(ClassLoader classLoader, String class_name, String method_name, Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = classLoader.loadClass(class_name);
            Method getInstance = obj_class.getDeclaredMethod("getInstance",new Class[]{});
            getInstance.setAccessible(true);
            Object singleton = getInstance.invoke(null, new Object[]{});

            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(singleton, pareVaules);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Object getFieldOjbect(String class_name,Object obj, String filedName){
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static Object getFieldOjbect(ClassLoader classLoader, String class_name,Object obj, String filedName){
        try {
            Class obj_class = classLoader.loadClass(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static Object getFieldOjbect(Class obj_class,Object obj, String filedName){
        try {
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static Object getStaticFieldOjbect(String class_name, String filedName){

        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Object getStaticFieldOjbect(ClassLoader classLoader, String class_name, String filedName){

        try {

            Class obj_class = classLoader.loadClass(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldOjbect(String classname, String filedName, Object obj, Object filedVaule){
        try {
            Class obj_class = Class.forName(classname);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(obj, filedVaule);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setFieldOjbect(Class obj_class, String filedName, Object obj, Object filedVaule){
        try {
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(obj, filedVaule);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setStaticOjbect(String class_name, String filedName, Object filedVaule){
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(null, filedVaule);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Object newObject(String class_name){
        try {
            Class obj_class = Class.forName(class_name);
            return obj_class.newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static Object newObject(Class obj_class){
        try {
            return obj_class.newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static Object newObject(ClassLoader classLoader, String class_name){
        try {
            Class obj_class = classLoader.loadClass(class_name);
            return obj_class.newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static Class getClass(String class_name){
        try {
            return Class.forName(class_name);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Class getClass(ClassLoader classLoader, String class_name){
        try {
            return classLoader.loadClass(class_name);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
