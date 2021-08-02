package com.example.edushareproyect;

public class RestApiMehotds {
    private static final String ipaddress = "http://3.128.217.135:1880";
    public static final String ApiGetUrl = ipaddress+"/WSCurso/listaempleados.php";
    public static final String ApiPostUrl = ipaddress+"/WSCurso/crear.php";
    public static final String ApiImageUrl = ipaddress+"/WSCurso/UploadFile.php";

    /*
    * Metodos de registro
    * */
    public static final String ApiGETCarreras = ipaddress+"/api/carreras/lista";
    public static final String ApiGETCampus = ipaddress+"/api/campus/lista";
    public static final String ApiPOSTAlumno = ipaddress+"/api/registro/alumno";
    public static final String ApiPOSTSesionMail = ipaddress+"/api/sesion/mail";


     /**
     * Session (Controles para inicio, validacion y finalizacion de sesion)
     **/
     public static final String ApiPOSTLogin = ipaddress+"/api/session/login";

}
