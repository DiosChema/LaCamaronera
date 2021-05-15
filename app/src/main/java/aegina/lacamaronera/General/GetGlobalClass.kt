package aegina.lacamaronera.General

import aegina.lacamaronera.DB.Query
import android.content.Context

class GetGlobalClass
{
    fun globalClass(context: Context): GlobalClass
    {

        var globalVariable: GlobalClass = context as GlobalClass

        if(globalVariable.user == null)
        {
            val query = Query()
            globalVariable.user = query.getUserDataBase(context)
        }

        return globalVariable
    }
}