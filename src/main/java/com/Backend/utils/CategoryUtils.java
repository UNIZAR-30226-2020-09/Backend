package com.Backend.utils;

import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.repository.ICatRepo;
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

    //Primera en crearse al crear usuario, menor id
    public static Category getSinCategoria(ICatRepo repoCat, User usuario) {
        List<Category> categorias = repoCat.findByUsuarioOrderByIdAsc(usuario);
        return categorias.get(0);
    }
}
