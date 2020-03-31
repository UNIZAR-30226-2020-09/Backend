package com.Backend.utils;

import com.Backend.model.Category;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.List;

public class CategoryUtils {

    public static JSONArray arrayCategorias(List<Category> categorias, boolean editables){
        JSONArray jsa = new JSONArray();
        for (Category cat : categorias) {
            JSONObject obj = new JSONObject();
            obj.put("catId", cat.getId());
            obj.put("categoryName", cat.getCategoryName());
            jsa.add(obj);
        }
        return jsa;
    }
}
