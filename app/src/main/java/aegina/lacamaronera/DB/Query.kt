package aegina.lacamaronera.DB

import aegina.lacamaronera.General.GlobalClass
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
            data.put("idEmpleado", saleObj.idEmpleado)
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

        val getSale = query.rawQuery("select idVenta, fecha, idEmpleado from Ventas", null)

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
                        getSale.getInt(2)
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

        db.close()

        return dishesList
    }

    fun insertDishes(contextTmp: Context, dishesTmp: MutableList<DishesObj>)
    {
        val db = DB(contextTmp, "Admin", null, 1)
        val query = db.writableDatabase
        val data = ContentValues()

        query.execSQL("delete from IngredientesRespaldo")
        query.execSQL("delete from PlatillosRespaldo")

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

        db.close()
    }

    fun cleanDishes(contextTmp: Context)
    {
        val db = DB(contextTmp, "Admin", null, 1)
        val query = db.writableDatabase

        query.execSQL("delete from Ventas")
        query.execSQL("delete from Platillos")
        query.execSQL("delete from Ingredientes")

        db.close()
    }

    fun insertUser(
        contextTmp: Context,
        user: User,
        password: String
    )
    {
        val db = DB(contextTmp, "Admin", null, 1)
        val query = db.writableDatabase
        val data = ContentValues()

        query.execSQL("delete from User")

        data.put("idEmpleado", user.idEmpleado)
        data.put("user", user.user)
        data.put("nombre", user.nombre)
        data.put("admin", if(user.admin) 1 else 0)
        data.put("password", password)

        query.insert("User", null, data)
        data.clear()

        db.close()
    }

    fun getUser(
        contextTmp: Context,
        email:String,
        password:String
    ): User
    {
        val db = DB(contextTmp, "Admin", null, 1)
        val query = db.writableDatabase

        var user = User(-1, "","",false)

        val getDishes = query.rawQuery("select idEmpleado, user, nombre, admin from User where user = $email, password = $password", null)

        if (getDishes.moveToFirst())
        {
            while (!getDishes.isAfterLast)
            {
                user = User(
                    getDishes.getInt(0),
                    getDishes.getString(1),
                    getDishes.getString(2),
                    getDishes.getInt(3) == 1
                )
            }
        }

        db.close()

        return user
    }

    fun getUserDataBase(
        contextTmp: Context
    ): User
    {
        val db = DB(contextTmp, "Admin", null, 1)
        val query = db.writableDatabase

        var user = User(-1, "","",false)

        val getDishes = query.rawQuery("select idEmpleado, user, nombre, admin from User", null)

        if (getDishes.moveToFirst())
        {
            while (!getDishes.isAfterLast)
            {
                user = User(
                    getDishes.getInt(0),
                    getDishes.getString(1),
                    getDishes.getString(2),
                    getDishes.getInt(3) == 1
                )
            }
        }

        db.close()

        return user
    }

}