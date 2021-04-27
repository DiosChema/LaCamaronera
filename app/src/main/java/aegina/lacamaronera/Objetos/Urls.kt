package aegina.lacamaronera.Objetos

data class Urls(
    var url:String,
    var endPointsGrupo:UrlsGrupos,
    var endPointsIngredientes:UrlsIngredientes,
    var endPointsImagenes: UrlsImagenes
)
{
    constructor(): this(
        "https://pocketsale.us-3.evennode.com/Camaroneria/",
        UrlsGrupos(
            "Familia/ObtenerFamilias",
            "Familia/AltaFamilia",
            "Familia/ActualizarFamilia",
            "Familia/EliminarFamilia"
        ),
        UrlsIngredientes(
            "Ingrediente/AltaIngrediente",
            "Ingrediente/ConsultaIngredientes"
        ),
        UrlsImagenes(
            "Imagen/subirImagen",
            "Imagen/obtenerImagen?image="
        )
    )
}

data class UrlsGrupos(
    val endPointObtenerGrupos:String,
    val endPointAltaGrupo:String,
    val endPointActualizarFamilia:String,
    val endPointEliminarFamilia:String
)

data class UrlsIngredientes(
    val endPointAltaIngrediente:String,
    val endPointObtenerIngrediente:String
)

data class UrlsImagenes(
    val endPointAltaImagen:String,
    val endPointObtenerImagen:String
)