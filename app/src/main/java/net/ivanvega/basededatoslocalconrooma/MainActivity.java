package net.ivanvega.basededatoslocalconrooma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import net.ivanvega.basededatoslocalconrooma.data.AppDatabase;
import net.ivanvega.basededatoslocalconrooma.data.User;
import net.ivanvega.basededatoslocalconrooma.data.UserDao;

public class MainActivity extends AppCompatActivity {

    Button btnIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///INSERCION (INSERT)
        btnIn = findViewById(R.id.btnInsert);
        btnIn.setOnClickListener(view -> {
            AppDatabase db =
                    AppDatabase.getDatabaseInstance(getApplication());

            UserDao dao = db.userDao();

            AppDatabase.databaseWriteExecutor.execute(() -> {
                User u = new User();

                //u.uid = 0;
                u.firstName = "John";
                u.lastName = "Cena";

                dao.insertAll(u);
                /*
                Toast.makeText(this,
                        "Insertado",
                        Toast.LENGTH_LONG).show();

                 */
                Log.d("DBUsuario", "Elemento insertado");
            });
        });

        ///CONSULTA SELECCIONA A TODOS (SELECT EVERYTHING QUERY)
        findViewById(R.id.btnQuery).setOnClickListener(view -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(getApplication());
            UserDao dao = db.userDao();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                AppDatabase.databaseWriteExecutor.execute(() -> {
                    dao.getAll().stream().forEach(user -> {
                        Log.i("Consulta User",
                                user.uid + " " + user.firstName);
                    });
                });


            } else {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    for (User user : dao.getAll()) {
                        Log.d("DBUsuario", user.firstName + " " + user.lastName);
                    }
                });
            }
        });

        ///DELETE
        findViewById(R.id.btnDelete).setOnClickListener(
                view -> {
                    AppDatabase db =
                            AppDatabase.getDatabaseInstance(getApplication());

                    UserDao dao = db.userDao();

                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        User u = new User();

                        u.uid = 20;
                        u.firstName = "John";
                        u.lastName = "Cena";

                        int elementos = dao.deleteUser(u);

                        Log.d("USuarios eliminados", "Elementos eliminados: " + elementos);
                    });
                }
        );

        //QUERY POR NOMBRE Y APELLIDO
        findViewById(R.id.btnSelect2).setOnClickListener(view -> {

            AppDatabase db = AppDatabase.getDatabaseInstance(getApplication());
            UserDao dao = db.userDao();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                AppDatabase.databaseWriteExecutor.execute(() -> {
                    User user = dao.findByName("John", "Cena");
                    Log.i("User por nombre: ", user.uid + " " + user.firstName + " " + user.lastName);


                    /*

                    dao.getAll().stream().forEach(user -> {
                        Log.i("Consulta User",
                                user.uid + " " + user.firstName);
                    });

                     */
                });

            } else {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    User user = dao.findByName("John", "Cena");
                    Log.i("User por nombre: ", user.uid + " " + user.firstName);
                });
            }
        });

    }
}