package com.Backend.utils;

import java.util.Random;

public class PasswordCheckUtils {

    private static final String minus = "abcdefghijklmnopqrstuvwxyz";
    private static final String mayus = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String digit = "0123456789";
    private static final String others = "@#$%!@#$%!";

    public static String generateStrongPassword(boolean min, boolean may, boolean numbers, boolean other, int longitud){

        Random r = new Random();
        String alphabet = "";
        StringBuilder password = new StringBuilder();

        alphabet = min ? alphabet + minus : alphabet;
        alphabet = may ? alphabet + mayus : alphabet;
        alphabet = numbers ? alphabet + digit : alphabet;
        alphabet = other ? alphabet + others : alphabet;

        for (int i = 0; i < longitud; i++) {
            password.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }

        return password.toString();
    }

    // Devuelve un entero entre 1 y 100 que denota el grado de robustez de password
    public static int gradoRobustez(String password){

        int puntosCaracter = 8;


        int puntuacion = 0;
        int mayusculas = 0;
        int minusculas = 0;
        int numeros = 0;
        int otros = 0;

        for(int i = 0; i < password.length(); ++i){
            if(password.charAt(i) >= 'A' && password.charAt(i) <= 'Z'){
                ++mayusculas;
            }
            else if(password.charAt(i) >= 'a' && password.charAt(i) <= 'z'){
                ++minusculas;
            }
            else if(password.charAt(i) >= '0' && password.charAt(i) <= '9'){
                ++numeros;
            }
            else{
                ++otros;
            }
        }

        puntuacion = (mayusculas > 0) ? puntuacion + 10 + puntosCaracter*mayusculas : puntuacion - 5;
        puntuacion = (minusculas > 0) ? puntuacion + 10 + puntosCaracter*minusculas : puntuacion - 5;
        puntuacion = (numeros > 0) ? puntuacion + 10 + puntosCaracter*numeros : puntuacion - 5;
        puntuacion = (otros > 0) ? puntuacion + 20 + puntosCaracter*otros : puntuacion - 5;
        puntuacion = (asciiConsecutivosOIguales(password, 1) && puntuacion > 50) ? puntuacion - 15 : puntuacion;
        puntuacion = (asciiConsecutivosOIguales(password, 0)) ? puntuacion - 15 : puntuacion;
        puntuacion = (password.length() < 8) ? puntuacion - 10 : puntuacion;
        puntuacion = (password.length() < 6) ? puntuacion - 15 : puntuacion;
        puntuacion = Math.min(puntuacion, 100);
        puntuacion = Math.max(0,puntuacion);

        return puntuacion;
    }

    // True si y solo si existen 4 minusculas, mayusculas o numeros consecutivos o mas en pass.
    // notIguales debe tomar valor 0 o 1.
    public static boolean asciiConsecutivosOIguales(String pass, int consecutivos){

        int nConsecutives = 0;
        char lastAscii = '0';
        boolean found = false;

        for(int i = 0; i < pass.length(); ++i){
            if(nConsecutives == 0 && rangoAlfanum(pass.charAt(i))){
                lastAscii = pass.charAt(i);
                nConsecutives++;
            }
            else if(nConsecutives > 0 && rangoAlfanum(pass.charAt(i)) && lastAscii + consecutivos == pass.charAt(i)){
                lastAscii = pass.charAt(i);
                nConsecutives ++;
            }
            else{
                nConsecutives = 0;
            }

            if(nConsecutives >= 4)
                found = true;

        }
        return found;
    }

    // True si y solo si c es un caracter alfanumérico
    public static boolean rangoAlfanum(char c){
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
