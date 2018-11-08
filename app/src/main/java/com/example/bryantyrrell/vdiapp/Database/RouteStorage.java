package com.example.bryantyrrell.vdiapp.Database;

import java.util.ArrayList;

public class RouteStorage {

    ArrayList<String> routeNames=new ArrayList();


    public RouteStorage(){

    }

   public ArrayList<String> getRouteNames(){
        return routeNames;
    }
    public void setRouteNames(String routename){
        routeNames.add(routename);
    }
}
