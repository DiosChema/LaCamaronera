package aegina.lacamaronera.Objetos

data class Urls(
    var url:String,
    var endPointsGrupo:UrlsGrupos
)
{
    constructor(): this(
        "https://pocketsale.us-3.evennode.com/Camaroneria/",
        /*"http://pvgestordeinventario-env.eba-p2bc44jy.us-east-1.elasticbeanstalk.com/",*/
        UrlsGrupos(
            "Familias/ObtenerFamilias",
            "Familias/AltaFamilia",
            "Familias/ActualizarFamilia",
            "Familias/EliminarFamilia"
        )
    )
}

data class UrlsGrupos(
    val endPointObtenerGrupos:String,
    val endPointAltaGrupo:String,
    val endPointActualizarFamilia:String,
    val endPointEliminarFamilia:String
)