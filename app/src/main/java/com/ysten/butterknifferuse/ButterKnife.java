package com.ysten.butterknifferuse;

import android.app.Activity;

/**
 * desc    :
 * time    : 2018/6/7 0007 17:47
 *
 * @author : wangjitao
 */
public class ButterKnife {
   public static void bind(Activity activity){
       String className = activity.getClass().getName()+"$ViewBinder";
       try {
           Class<?> viewBindClass = Class.forName(className);
           ViewBinder viewBinder  = (ViewBinder) viewBindClass.newInstance();
           viewBinder.bind(activity);
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       } catch (IllegalAccessException e) {
           e.printStackTrace();
       } catch (InstantiationException e) {
           e.printStackTrace();
       }
   }
}
