package net.ivanvega.basededatoslocalconrooma.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.InetAddresses;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.ivanvega.basededatoslocalconrooma.data.AppDatabase;
import net.ivanvega.basededatoslocalconrooma.data.User;
import net.ivanvega.basededatoslocalconrooma.data.UserDao;

import java.util.List;

public class MiContentProvider extends ContentProvider {
    /*Estructura de mi uri:
        uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user  -> insert y query
        uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user/#  -> update, query y delete
        uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user/*  -> query, update y delete}
                        net.ivanvega.basededatoslocalconrooma.provider
     */

    //Se crea un objeto de uri matcher
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        /*
         * Se colocan aquí las llamadas al addURI() para todos los patrones de URI que debe de reconocer el
         * proveedor. LAs que se encuentran aqui son las relacionadas a la tabla user.
         */


        sURIMatcher.addURI("net.ivanvega.basededatoslocalconrooma.provider","user", 1);
        sURIMatcher.addURI("net.ivanvega.basededatoslocalconrooma.provider","user/#", 2);
        sURIMatcher.addURI("net.ivanvega.basededatoslocalconrooma.provider","user/*", 3);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    //Define una matríz para almacenar los datos que retornan los métodos en la DB con ROOM
    private Cursor listUserToCursorUser(List<User> usuarios){

        //Crea la matriz con las columnas disponibles deacuerdo a la tabla disponible en DB ROOM
        MatrixCursor cursor = new MatrixCursor(new String[]{
                "uid","first_name","last_name"
        });

        /*
        * Añade valor por valor contenido de la lista que le llega al método hacia la matriz,
        * posiciona los valores en cada columna
        * */
        for(User usuario: usuarios){
            cursor.newRow().add("uid", usuario.uid)
                    .add("first_name", usuario.firstName)
                    .add("last_name", usuario.lastName);
        }
        return cursor;
    }

    private Cursor userToCursorUser(User user){
        //Crea la matriz con las columnas disponibles deacuerdo a la tabla disponible en DB ROOM
        MatrixCursor cursor = new MatrixCursor(new String[]{
                "uid","first_name","last_name"
        });

        /*
         * Añade valor por valor contenido de la lista que le llega al método hacia la matriz,
         * posiciona los valores en cada columna
         * */
        cursor.newRow().add("uid", user.uid)
                .add("first_name", user.firstName)
                .add("last_name", user.lastName);

        return cursor;
    }


    /*
    * Implementación de consultas
    * */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        AppDatabase db = AppDatabase.getDatabaseInstance(getContext());

        Cursor cursor= null;
        UserDao dao = db.userDao();

        switch (sURIMatcher.match(uri)){
            // Si la URI que viene es para TODA LA TABLA USER
            case 1:
                //cuando no tenga una condicion de seleccion, entonces es un Selec * from User
                if(TextUtils.isEmpty(selection)){
                    cursor = listUserToCursorUser(dao.getAll());
                }else{
                    /*
                    * Cuando tenga una condición de selección, en este caso por el nombre apellido
                    * El metodo me regresa un usuario, creo una lista de usuario paraq ue me funcione el cursor
                     * */

                    try{
                        cursor = userToCursorUser(dao.findByName(selectionArgs[0],selectionArgs[1]));
                    }catch (NullPointerException e){
                        Log.i("Error","No se puedo hacer la consulta ");
                    }
                }
                break;
            //Si la uri que viene es para una FILA ESPECIFICA
            case 2:
                break;
            case 3:
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        String typeMime = "";

        switch (sURIMatcher.match(uri)) {
            case 1:
                typeMime = "vnd.android.cursor.dir/vnd.net.ivanvega.basededatoslocalconrooma.provider.user";
                break;
            case 2:
                typeMime = "vnd.android.cursor.item/vnd.net.ivanvega.basededatoslocalconrooma.provider.user";
                break;
            case 3:

                typeMime = "vnd.android.cursor.dir/vnd.net.ivanvega.basededatoslocalconrooma.provider.user";
                break;
        }
        return typeMime;
    }


    /*
    * INSERT
    * */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues contentValues) {
        AppDatabase db =
                AppDatabase.getDatabaseInstance(getContext());
        Cursor cursor= null;
        UserDao dao = db.userDao();
        User usuario= new User();;
        switch (sURIMatcher.match(uri)){
            case 1:

                usuario.firstName = contentValues.getAsString(UsuarioContrato.COLUMN_FIRSTNAME);
                usuario.lastName = contentValues.getAsString(UsuarioContrato.COLUMN_LASTNAME);

                long  newid = dao.insert(usuario);
                return  Uri.withAppendedPath(uri, String.valueOf(newid));

        }

        return   Uri.withAppendedPath(uri, String.valueOf( -1))  ;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selectionClause,
                      @Nullable String[] selectionArgs) {

        AppDatabase db = AppDatabase.getDatabaseInstance(getContext());
        UserDao userDao = db.userDao();
        int id = Integer.parseInt(uri.getLastPathSegment()), rowsAfected = 0;

        switch (sURIMatcher.match(uri)){
            case 2:
                //Busco primero el usuario a eliminar, pues el metodo delete en el USER DAO pide como argumento un User
                List<User> userDelete  =  userDao.loadAllByIds(new int[]{id});
                //Le mando ese usuario encontrado al metodo deleteUser
                rowsAfected = userDao.deleteUser(userDelete.get(0));
                break;
        }
        //regreso la lineas afectadas
        return rowsAfected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                          @Nullable String s, @Nullable String[] strings) {

         int id  = Integer.parseInt( uri.getLastPathSegment());

        AppDatabase db =
                AppDatabase.getDatabaseInstance(getContext());
        Cursor cursor= null;
        UserDao dao = db.userDao();

        List<User> usuarioUpdate  =  dao.loadAllByIds(new int[]{id});

        usuarioUpdate.get(0).firstName =
                contentValues.getAsString(UsuarioContrato.COLUMN_FIRSTNAME);
        usuarioUpdate.get(0).lastName =
                contentValues.getAsString(UsuarioContrato.COLUMN_LASTNAME);

        return dao.updateUser(usuarioUpdate.get(0));
    }

}
