package aegina.lacamaronera.DB

import aegina.lacamaronera.Objetos.*
import android.content.ContentValues
import android.content.Context
import java.lang.Exception

class Query
{
    fun insertSale(contextTmp: Context, saleObj: SaleObj): Boolean {
        try
        {
            var idSale = 0
            val db = DB(contextTmp, "Admin", null, 1)

            val query = db.writableDatabase
            val data = ContentValues()

            data.put("fecha", saleObj.fecha)
            query.insert("Ventas", null, data)
            data.clear()

            val getSale = query.rawQuery("select idVenta from Ventas order by idVenta DESC", null)

            getSale.moveToFirst()
            idSale = getSale.getInt(0)

            for(dishSaleObjTmp: DishSaleObj in saleObj.platillos)
            {
                data.put("idVenta", idSale)
                data.put("idPlatillo", dishSaleObjTmp.idPlatillo)
                data.put("cantidad", dishSaleObjTmp.cantidad)
                data.put("nombre", dishSaleObjTmp.nombre)
                data.put("precio", dishSaleObjTmp.precio)
                query.insert("Platillos", null, data)
                data.clear()

                for(ingredientDishObjTmp: IngredientDishObj in dishSaleObjTmp.ingredientes)
                {
                    data.put("idVenta", idSale)
                    data.put("idIngrediente", ingredientDishObjTmp.idIngrediente)
                    data.put("cantidad", ingredientDishObjTmp.cantidad)
                    query.insert("Ingredientes", null, data)
                    data.clear()
                }
            }

            db.close()

            return true

        }
        catch (e: Exception)
        {
            return false
        }
    }

    fun getLocalSales(contextTmp: Context) : ArrayList<SaleObj>
    {
        val sales = ArrayList<SaleObj>()

        val db = DB(contextTmp, "Admin", null, 1)

        val query = db.writableDatabase

        val getSale = query.rawQuery("select idVenta, fecha from Ventas", null)

        if (getSale.moveToFirst())
        {
            while (!getSale.isAfterLast) {

                val dishes = ArrayList<DishSaleObj>()
                val getDish = query.rawQuery("select idPlatillo, cantidad, precio, nombre from Platillos where idVenta = ${getSale.getInt(0)}", null)

                if(getDish.moveToFirst())
                {
                    while (!getDish.isAfterLast) {
                        val ingredients = ArrayList<IngredientDishObj>()
                        val getIngredient = query.rawQuery("select idIngrediente, cantidad from Ingredientes where idVenta = ${getSale.getInt(0)}", null)

                        if(getIngredient.moveToFirst())
                        {
                            while (!getIngredient.isAfterLast) {
                                ingredients.add(IngredientDishObj(getIngredient.getInt(0), getIngredient.getDouble(1)))
                                getIngredient.moveToNext()
                            }
                        }

                        dishes.add(
                            DishSaleObj(
                                getDish.getInt(0),
                                getDish.getDouble(1),
                                getDish.getDouble(2),
                                getDish.getString(3),
                                ingredients
                            ))

                        getDish.moveToNext()
                    }
                }

                sales.add(
                    SaleObj(
                        getSale.getInt(0),
                        dishes,
                        0.0,
                        getSale.getString(1),
                        1
                    )
                )

                getSale.moveToNext()
            }
        }

        db.close()

        return sales
    }

    fun getDishes(contextTmp: Context): ArrayList<DishesObj>
    {
        val dishesList = ArrayList<DishesObj>()

        val db = DB(contextTmp, "Admin", null, 1)
        val query = db.writableDatabase

        val getDishes = query.rawQuery("select idPlatillo, nombre, precio, idFamilia, descripcion from PlatillosRespaldo", null)

        if (getDishes.moveToFirst())
        {
            while (!getDishes.isAfterLast)
            {
                dishesList.add(DishesObj(
                    getDishes.getInt(0),
                    getDishes.getString(1),
                    getDishes.getDouble(2),
                    ArrayList(),
                    getDishes.getInt(3),
                    getDishes.getString(4)
                ))

                getDishes.moveToNext()
            }
        }

        val getIngredient = query.rawQuery("select idPlatillo, idIngrediente, cantidad from IngredientesRespaldo", null)

        if (getIngredient.moveToFirst())
        {
            while (!getIngredient.isAfterLast)
            {
                val ingredient = IngredientDishObj(
                    getIngredient.getInt(1),
                    getIngredient.getDouble(2)
                )

                val idPlatillo = getIngredient.getInt(0)

                for (i in 0 until dishesList.size)
                {
                    if(idPlatillo == dishesList[i].idPlatillo)
                    {
                        dishesList[i].ingredientes.add(ingredient)
                        break
                    }
                }

                getIngredient.moveToNext()
            }
        }

        return dishesList
    }

    fun insertDishes(contextTmp: Context, dishesTmp: MutableList<DishesObj>)
    {
        val db = DB(contextTmp, "Admin", null, 1)
        val query = db.writableDatabase
        val data = ContentValues()

        for(dishTmp: DishesObj in dishesTmp)
        {
            for(ingredient: IngredientDishObj in dishTmp.ingredientes)
            {
                data.put("idPlatillo", dishTmp.idPlatillo)
                data.put("idIngrediente", ingredient.idIngrediente)
                data.put("cantidad", ingredient.cantidad)
                query.insert("IngredientesRespaldo", null, data)
                data.clear()
            }

            data.put("idPlatillo", dishTmp.idPlatillo)
            data.put("nombre", dishTmp.nombre)
            data.put("precio", dishTmp.precio)
            data.put("idFamilia", dishTmp.idFamilia)
            data.put("descripcion", dishTmp.descripcion)
            query.insert("PlatillosRespaldo", null, data)
            data.clear()
        }
    }

    fun cleanDishes(contextTmp: Context)
    {
        val db = DB(contextTmp, "Admin", null, 1)
        val query = db.writableDatabase

        query.execSQL("delete from Ventas")
        query.execSQL("delete from Platillos")
        query.execSQL("delete from Ingredientes")
    }
}