package net.ivanvega.basededatoslocalconrooma.provider;

import android.net.Uri;

/*
 * Contiene definiciones de constantes para URI, nombres de columnas, tipos de MIME y
 * otros metadatos que le pertenecen al proveedor. La clase establece un contrato entre el
 * proveedor y otras aplicaciones al asegurarse de que se pueda acceder correctamente al proveedor
 * aunque se hayan producido cambios en los valores de los URI, los nombres de las columnas, etc.
 *
 * */

public class UsuarioContrato {
    public static final Uri CONTENT_URI =
            Uri.parse("content://net.ivanvega.basededatoslocalconrooma.provider/user");

    //Columnas existentes en la DB con Room para la tabla de usuarios
    public static final String COLUMN_ID = "uid";
    public static final String COLUMN_FIRSTNAME = "first_name";
    public static final String COLUMN_LASTNAME = "last_name";

    public static final String[] COLUMNS_NAME = new String[]{
            "uid", "first_name", "last_name"
    };
}