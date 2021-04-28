package aegina.lacamaronera.Objetos

data class Urls(
    var url:String,
    var endPointsGrupo:UrlsGrupos,
    var endPointsIngredientes:UrlsIngredientes,
    var endPointsImagenes: UrlsImagenes,
    var endPointDishes: UrlsDishes
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
            "Ingrediente/ConsultaIngredientes",
            "Ingrediente/ConsultarIngrediente",
            "Ingrediente/ActualizaIngrediente",
            "Ingrediente/BajaIngrediente"
        ),
        UrlsImagenes(
            "Imagen/subirImagen",
            "Imagen/obtenerImagen?image="
        ),
        UrlsDishes(
            "Inventario/ConsultarPlatillos"
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
    val endPointObtenerIngredientes:String,
    val endPointObtenerIngrediente:String,
    val endPointActualizarIngrediente:String,
    val endPointEliminarIngrediente:String
)

data class UrlsImagenes(
    val endPointAltaImagen:String,
    val endPointObtenerImagen:String
)

data class UrlsDishes(
    val endPointConsultarPlatillos: String
)