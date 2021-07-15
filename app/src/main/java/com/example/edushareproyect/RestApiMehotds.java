package com.example.edushareproyect;

public class RestApiMehotds {
    private static final String ipaddress = "18.116.112.28:1880";
    public static final String ApiGetUrl = "http://"+ipaddress+"/WSCurso/listaempleados.php";
    public static final String ApiPostUrl = "http://"+ipaddress+"/WSCurso/crear.php";
    public static final String ApiImageUrl = "http://"+ipaddress+"/WSCurso/UploadFile.php";

     /**
     * Session (Controles para inicio, validacion y finalizacion de sesion)
     **/
     public static final String ApiPOSTLogin = "http://"+ipaddress+"/api/session/login";
}
