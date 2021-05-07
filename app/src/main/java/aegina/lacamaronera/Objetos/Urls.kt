package aegina.lacamaronera.Objetos

data class Urls(
    var url:String,
    var endPointsGrupo:UrlsGrups,
    var endPointsIngredientes:UrlsIngredients,
    var endPointsImagenes: UrlsImages,
    var endPointDishes: UrlsDishes,
    var endPointAssorment: UrlsAssorment,
    var endPointSale: UrlsSale
)
{
    constructor(): this(
        "https://pocketsale.us-3.evennode.com/Camaroneria/",
        UrlsGrups(
            "Familia/ObtenerFamilias",
            "Familia/AltaFamilia",
            "Familia/ActualizarFamilia",
            "Familia/EliminarFamilia"
        ),
        UrlsIngredients(
            "Ingrediente/AltaIngrediente",
            "Ingrediente/ConsultaIngredientes",
            "Ingrediente/ConsultarIngrediente",
            "Ingrediente/ActualizaIngrediente",
            "Ingrediente/BajaIngrediente"
        ),
        UrlsImages(
            "Imagen/subirImagen",
            "Imagen/obtenerImagen?image="
        ),
        UrlsDishes(
            "Inventario/ConsultarPlatillos",
            "Inventario/AltaPlatillo",
            "Inventario/ActualizaPlatillo",
            "Inventario/ConsultaPlatillo",
            "Inventario/BajaPlatillo"
        ),
        UrlsAssorment(
            "Gasto/AltaGastoIngrediente",
            "Gasto/consultaGasto"
        ),
        UrlsSale(
            "Venta/AltaVenta",
            "Venta/ConsultaVenta",
            "Venta/AltaVariasVentas"
        )
    )
}

data class UrlsGrups(
    val endPointObtenerGrupos:String,
    val endPointAltaGrupo:String,
    val endPointActualizarFamilia:String,
    val endPointEliminarFamilia:String
)

data class UrlsIngredients(
    val endPointAltaIngrediente:String,
    val endPointObtenerIngredientes:String,
    val endPointObtenerIngrediente:String,
    val endPointActualizarIngrediente:String,
    val endPointEliminarIngrediente:String
)

data class UrlsImages(
    val endPointAltaImagen:String,
    val endPointObtenerImagen:String
)

data class UrlsDishes(
    val endPointConsultarPlatillos: String,
    val endPointAltaPlatillos: String,
    val endPointActualizarPlatillos:String,
    val endPointConsultaPlatillo:String,
    val endPointBajaPlatillo:String
)

data class UrlsAssorment(
    val endPointPostAssorment: String,
    val endPointGetAssorment: String
)

data class UrlsSale(
    val endPointPostSale: String,
    val endPointGetSale: String,
    val endPointPostSales: String
)