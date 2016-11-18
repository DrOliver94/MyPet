package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class MyPetDB {

    // database constants
    public static final String DB_NAME = "mypetdb.db";
    public static final int    DB_VERSION = 1;

    //################### USERS table constants ######################
    public static final String USERS_TABLE = "users";

    public static final String USERS_ID = "IdUser";
    public static final int    USERS_ID_COL = 0;

    public static final String USERS_USERNAME = "UserName";
    public static final int    USERS_USERNAME_COL = 1;

    //meglio lasciarle sul server...
    //public static final String USERS_PASSWORD = "Password";
    //public static final int    USERS_PASSWORD_COL = 2;

    public static final String USERS_PROFILEPIC = "ProfilePic";
    public static final int    USERS_PROFILEPIC_COL = 2;

    public static final String USERS_NAME = "Name";
    public static final int    USERS_NAME_COL = 3;

    public static final String USERS_SURNAME = "Surname";
    public static final int    USERS_SURNAME_COL = 4;

    public static final String USERS_GENDER = "Gender";
    public static final int    USERS_GENDER_COL = 5;

    public static final String USERS_BIRTHDATE = "BirthDate";
    public static final int    USERS_BIRTHDATE_COL = 6;

    //##################### POSTS table #############################
    public static final String POSTS_TABLE = "post";

    public static final String POSTS_ID = "IdPost";
    public static final int    POSTS_ID_COL = 0;

    public static final String POSTS_IDAUTHOR = "IdAuthor";
    public static final int    POSTS_IDAUTHOR_COL = 1;

    public static final String POSTS_PICTURE = "Picture";
    public static final int    POSTS_PICTURE_COL = 2;

    public static final String POSTS_TEXT = "Text";
    public static final int    POSTS_TEXT_COL = 3;

    public static final String POSTS_PLACE = "Place";
    public static final int    POSTS_PLACE_COL = 4;

//    public static final String POSTS_PLACE_LAT = "Latitude";
//    public static final int    POSTS_PLACE_LAT_COL = 5;
//
//    public static final String POSTS_PLACE_LON = "Longitude";
//    public static final int    POSTS_PLACE_LON_COL = 6;

    public static final String POSTS_DATE = "Date";
    public static final int    POSTS_DATE_COL = 5;

    //##################### ANIMALS table #############################
    public static final String ANIMALS_TABLE = "animals";

    public static final String ANIMALS_ID = "IdAnim";
    public static final int    ANIMALS_ID_COL = 0;

    public static final String ANIMALS_NAME = "AnimName";
    public static final int    ANIMALS_NAME_COL = 1;

    public static final String ANIMALS_SPECIES = "Species";
    public static final int    ANIMALS_SPECIES_COL = 2;

    public static final String ANIMALS_GENDER = "Gender";
    public static final int    ANIMALS_GENDER_COL = 3;

    public static final String ANIMALS_PROFILEPIC = "ProfilePic";
    public static final int    ANIMALS_PROFILEPIC_COL = 4;

    public static final String ANIMALS_BIRTHDATE = "BirthDate";
    public static final int    ANIMALS_BIRTHDATE_COL = 5;

    //##################### FRIENDSHIP table #############################
    public static final String FRIENDSHIP_TABLE = "friendship";

    public static final String FRIENDSHIP_ID = "IdFriendship";
    public static final int    FRIENDSHIP_ID_COL = 0;

    public static final String FRIENDSHIP_USERSENDER = "IdUserSender";
    public static final int    FRIENDSHIP_USERSENDER_COL = 1;

    public static final String FRIENDSHIP_USERRECEIVER = "IdUserReceiver";
    public static final int    FRIENDSHIP_USERRECEIVER_COL = 2;

    public static final String FRIENDSHIP_STATUS = "Status";
    public static final int    FRIENDSHIP_STATUS_COL = 3;

    //##################### POSTS-ANIMALS table #############################
    public static final String POSTANIMALS_TABLE = "postanimals";

    public static final String POSTANIMALS_IDPOST = "IdPost";
    public static final int    POSTANIMALS_IDPOST_COL = 0;

    public static final String POSTANIMALS_IDANIMAL = "IdAnim";
    public static final int    POSTANIMALS_IDANIMAL_COL = 1;

    //##################### POSTS-USERS table #############################
    public static final String POSTUSERS_TABLE = "postusers";

    public static final String POSTUSERS_IDPOST = "IdPost";
    public static final int    POSTUSERS_IDPOST_COL = 0;

    public static final String POSTUSERS_IDUSER = "IdUser";
    public static final int    POSTUSERS_IDUSER_COL = 1;

    //##################### USERS-ANIMALS table #############################
    public static final String USERSANIMALS_TABLE = "usersanimals";

    public static final String USERSANIMALS_IDUSER = "IdUser";
    public static final int    USERSANIMALS_IDUSER_COL = 0;

    public static final String USERSANIMALS_IDANIMAL = "IdAnim";
    public static final int    USERSANIMALS_IDANIMAL_COL = 1;

    //##################### REMINDERS table #############################
    public static final String REMINDERS_TABLE = "reminder";

    public static final String REMINDERS_IDREMINDER = "IdReminder";
    public static final int    REMINDERS_IDREMINDER_COL = 0;

    public static final String REMINDERS_IDUSER = "IdUser";
    public static final int    REMINDERS_IDUSER_COL = 1;

    public static final String REMINDERS_IDANIM = "IdAnim";
    public static final int    REMINDERS_IDANIM_COL = 2;

    public static final String REMINDERS_EVENTNAME = "EventName";
    public static final int    REMINDERS_EVENTNAME_COL = 3;

    public static final String REMINDERS_EVENTPLACE = "EventPlace";
    public static final int    REMINDERS_EVENTPLACE_COL = 4;

    public static final String REMINDERS_EVENTTIME = "EventTime";
    public static final int    REMINDERS_EVENTTIME_COL = 5;

    //##################### CREATE and DROP statements ##################
    public static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + USERS_TABLE + " (" +
                    USERS_ID         + " INTEGER PRIMARY KEY," +
                    USERS_USERNAME   + " TEXT    NOT NULL UNIQUE," +
                    USERS_PROFILEPIC + " TEXT," +
                    USERS_NAME       + " TEXT," +
                    USERS_SURNAME    + " TEXT," +
                    USERS_GENDER     + " TEXT," +
                    USERS_BIRTHDATE  + " TEXT)";

    public static final String CREATE_POSTS_TABLE =
            "CREATE TABLE " + POSTS_TABLE + " (" +
                    POSTS_ID       + " INTEGER PRIMARY KEY," +
                    POSTS_IDAUTHOR + " TEXT NOT NULL," +
                    POSTS_PICTURE  + " TEXT," +
                    POSTS_TEXT     + " TEXT," +
                    POSTS_PLACE    + " TEXT," +
                    POSTS_DATE     + " TEXT NOT NULL)";

    public static final String CREATE_ANIMALS_TABLE =
            "CREATE TABLE " + ANIMALS_TABLE + " (" +
                    ANIMALS_ID         + " INTEGER PRIMARY KEY," +
                    ANIMALS_NAME       + " TEXT    NOT NULL," +
                    ANIMALS_SPECIES    + " TEXT," +
                    ANIMALS_GENDER     + " TEXT," +
                    ANIMALS_PROFILEPIC + " TEXT," +
                    ANIMALS_BIRTHDATE  + " TEXT)";

    public static final String CREATE_FRIENDSHIP_TABLE =
            "CREATE TABLE " + FRIENDSHIP_TABLE + " (" +
                    FRIENDSHIP_ID           + " INTEGER PRIMARY KEY," +
                    FRIENDSHIP_USERSENDER   + " TEXT    NOT NULL," +
                    FRIENDSHIP_USERRECEIVER + " TEXT    NOT NULL," +
                    FRIENDSHIP_STATUS       + " TEXT    NOT NULL)";

    public static final String CREATE_POSTANIMALS_TABLE =
            "CREATE TABLE " + POSTANIMALS_TABLE + " (" +
                    POSTANIMALS_IDPOST   + " TEXT NOT NULL," +
                    POSTANIMALS_IDANIMAL + " TEXT NOT NULL)";

    public static final String CREATE_POSTUSERS_TABLE =
            "CREATE TABLE " + POSTUSERS_TABLE + " (" +
                    POSTUSERS_IDPOST + " TEXT NOT NULL," +
                    POSTUSERS_IDUSER + " TEXT NOT NULL)";

    public static final String CREATE_USERSANIMALS_TABLE =
            "CREATE TABLE " + USERSANIMALS_TABLE + " (" +
                    USERSANIMALS_IDUSER   + " TEXT NOT NULL," +
                    USERSANIMALS_IDANIMAL + " TEXT NOT NULL)";

    public static final String CREATE_REMINDERS_TABLE =
            "CREATE TABLE " + REMINDERS_TABLE + " (" +
                    REMINDERS_IDREMINDER + " INTEGER PRIMARY KEY," +
                    REMINDERS_IDUSER     + " TEXT NOT NULL," +
                    REMINDERS_IDANIM     + " TEXT NOT NULL," +
                    REMINDERS_EVENTNAME  + " TEXT NOT NULL," +
                    REMINDERS_EVENTPLACE + " TEXT NOT NULL," +
                    REMINDERS_EVENTTIME  + " TEXT NOT NULL)";

    public static final String DROP_USERS_TABLE =
            "DROP TABLE IF EXISTS " + USERS_TABLE;

    public static final String DROP_POSTS_TABLE =
            "DROP TABLE IF EXISTS " + POSTS_TABLE;

    public static final String DROP_ANIMALS_TABLE =
            "DROP TABLE IF EXISTS " + ANIMALS_TABLE;

    public static final String DROP_FRIENDSHIP_TABLE =
            "DROP TABLE IF EXISTS " + FRIENDSHIP_TABLE;

    public static final String DROP_POSTANIMALS_TABLE =
            "DROP TABLE IF EXISTS " + POSTANIMALS_TABLE;

    public static final String DROP_POSTUSERS_TABLE =
            "DROP TABLE IF EXISTS " + POSTUSERS_TABLE;

    public static final String DROP_USERSANIMALS_TABLE =
            "DROP TABLE IF EXISTS " + USERSANIMALS_TABLE;

    public static final String DROP_REMINDERS_TABLE =
            "DROP TABLE IF EXISTS " + REMINDERS_TABLE;

    private static class DBHelper extends SQLiteOpenHelper {

        public Context context;

        public DBHelper(Context context, String name,
                        CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.context=context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create tables
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_POSTS_TABLE);
            db.execSQL(CREATE_ANIMALS_TABLE);
            db.execSQL(CREATE_FRIENDSHIP_TABLE);
            db.execSQL(CREATE_POSTANIMALS_TABLE);
            db.execSQL(CREATE_POSTUSERS_TABLE);
            db.execSQL(CREATE_USERSANIMALS_TABLE);
            db.execSQL(CREATE_REMINDERS_TABLE);

            //insert sample post
            Log.d("MyPet", "INSERT Samples");

//            db.execSQL("INSERT INTO post " +
//                       "VALUES (1, 28, '', 'Ceci nest pas un post', '', '')");
//            db.execSQL("INSERT INTO post " +
//                    "VALUES (2, 29, '', 'Gatti ^^', '', '')");
//            Log.d("MyPet", "INSERT DrOliver");
//            db.execSQL("INSERT INTO users " +
//                    "VALUES (28, 'DrOliver', '', 'Matteo', 'Oliveri', 'male', '1994-07-08')");
//            Log.d("MyPet", "INSERT Sissy");
//            db.execSQL("INSERT INTO users " +
//                    "VALUES (29, 'Sissy', '', 'Silvia', 'Lombardo', 'female', '1993-12-08')");
//            Log.d("MyPet", "INSERT Axel");
//            db.execSQL("INSERT INTO animals " +
//                    "VALUES (8, 'Axel2121', 'Cane', 'male', '', '2012-06-15')");
//            Log.d("MyPet", "INSERT Robin");
//            db.execSQL("INSERT INTO animals " +
//                    "VALUES (12, 'Robin2121', 'Gatto', 'male', '', '2013-03-31')");
//            Log.d("MyPet", "INSERT Happy");
//            db.execSQL("INSERT INTO animals " +
//                    "VALUES (98, 'Happy2121', 'Cane', 'male', '', '2012-06-15')");
//            Log.d("MyPet", "INSERT Jerry");
//            db.execSQL("INSERT INTO animals " +
//                    "VALUES (134, 'Jerry221', 'Gatto', 'male', '', '2013-03-31')");
//            Log.d("MyPet", "INSERT Link Axel");
//            db.execSQL("INSERT INTO usersanimals " +
//                    "VALUES (28, 8)");
//            Log.d("MyPet", "INSERT Link Robin");
//            db.execSQL("INSERT INTO usersanimals " +
//                    "VALUES (29, 12)");
//            Log.d("MyPet", "INSERT Link Happy");
//            db.execSQL("INSERT INTO usersanimals " +
//                    "VALUES (28, 98)");
//            Log.d("MyPet", "INSERT LInk Jerry");
//            db.execSQL("INSERT INTO usersanimals " +
//                    "VALUES (29, 134)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("Task list", "Upgrading db from version "
                    + oldVersion + " to " + newVersion);

            db.execSQL(MyPetDB.DROP_USERS_TABLE);
            db.execSQL(MyPetDB.DROP_ANIMALS_TABLE);
            db.execSQL(MyPetDB.DROP_POSTS_TABLE);
            db.execSQL(MyPetDB.DROP_FRIENDSHIP_TABLE);
            db.execSQL(MyPetDB.DROP_POSTANIMALS_TABLE);
            db.execSQL(MyPetDB.DROP_POSTUSERS_TABLE);
            db.execSQL(MyPetDB.DROP_USERSANIMALS_TABLE);
            db.execSQL(MyPetDB.DROP_REMINDERS_TABLE);
            onCreate(db);
        }
    }

    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // constructor
    public MyPetDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    /**
     * Inserisce un utente nel DB. Se già presente, aggiorna i dati
     *
     * @param u utente da inserire nel DB
     * @return riga in cui si trova l'utente <br/>
     *         -1 in caso di errore
     */
    public long insertUser(User u) {
        Log.d("MyPet", "Insert user " + u.id + ", " + u.surname);
        //Controlla se l'utente è già nel DB
        String where = USERS_ID + "= ?";
        String[] whereArgs = { u.id };

        openReadableDB();
        Cursor cursor = db.query(USERS_TABLE, null,
                where, whereArgs, null, null, null);

        int presence = cursor.getCount();
        cursor.close();
        this.closeDB();

        //TODO forse meglio non ritornare presence

        if(presence > 0){        //Se l'utente è nel DB
//            return updateUser(u);
            return presence;    //Si rischia di cancellare info. Usare updateUser per aggiornare un utente.
        } else {
            ContentValues cv = new ContentValues();
            cv.put(USERS_ID, u.id);
            cv.put(USERS_NAME, u.name);
            cv.put(USERS_USERNAME, u.username);
            cv.put(USERS_SURNAME, u.surname);
            cv.put(USERS_GENDER, u.gender);
            cv.put(USERS_PROFILEPIC, u.profilepic);

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
            cv.put(USERS_BIRTHDATE, format.format(u.birthdate));

            this.openWriteableDB();
            long rowID = db.insert(USERS_TABLE, null, cv);
            this.closeDB();

            return rowID;
        }
    }

    public User getUser(String idUser) {
        String where = USERS_ID + "= ?";
        String[] whereArgs = { idUser };

        openReadableDB();
        Cursor cursor = db.query(USERS_TABLE, null,
                where, whereArgs, null, null, null);

        //lettura dei dati dell'utente
        cursor.moveToFirst();
        User user = new User();
        user.id = cursor.getString(USERS_ID_COL);
        user.username = cursor.getString(USERS_USERNAME_COL);
        user.name = cursor.getString(USERS_NAME_COL);
        user.surname = cursor.getString(USERS_SURNAME_COL);
        user.gender = cursor.getString(USERS_GENDER_COL);
        user.profilepic = cursor.getString(USERS_PROFILEPIC_COL);

        SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
        try {
            user.birthdate = format.parse(cursor.getString(USERS_BIRTHDATE_COL));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //TODO controllare per errori

        if (cursor != null)
            cursor.close();
        this.closeDB();

        return user;
    }

    public int updateUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(USERS_ID, user.id);
        cv.put(USERS_NAME, user.name);
        cv.put(USERS_SURNAME, user.surname);
        cv.put(USERS_GENDER, user.gender);
        cv.put(USERS_PROFILEPIC, user.profilepic);

        SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
        cv.put(USERS_BIRTHDATE, format.format(user.birthdate));

        String where = USERS_ID + "= ?";
        String[] whereArgs = { String.valueOf(user.id) };

        this.openWriteableDB();
        int rowCount = db.update(USERS_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    public long insertFriendship(String idFriendship, String idUser1, String idUser2, String status) {
        //Verifica se l'amicizia è già nel DB
        String where = FRIENDSHIP_ID + "= ?";
        String[] whereArgs = { idFriendship };
        openReadableDB();
        Cursor cursor = db.query(FRIENDSHIP_TABLE, null,
                where, whereArgs, null, null, null);

        int presence = cursor.getCount();
        cursor.close();
        this.closeDB();

        if(presence > 0){
            return presence;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(FRIENDSHIP_ID, idFriendship);
            cv.put(FRIENDSHIP_USERSENDER, idUser1);
            cv.put(FRIENDSHIP_USERRECEIVER, idUser2);
            cv.put(FRIENDSHIP_STATUS, status);

            this.openWriteableDB();
            long rowID = db.insert(FRIENDSHIP_TABLE, null, cv);
            this.closeDB();
            return rowID;
        }
    }

    public ArrayList<User> getFriendsByUser(String idUser) {
        ArrayList<User> friends = new ArrayList<>();
        openReadableDB();

        //Serve un join: usiamo QueryBuilder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(USERS_TABLE + " JOIN " + FRIENDSHIP_TABLE +
                " ON (" + USERS_TABLE + "." + USERS_ID + "=" + FRIENDSHIP_TABLE + "." + FRIENDSHIP_USERSENDER +
                " OR " + USERS_TABLE + "." + USERS_ID + "=" + FRIENDSHIP_TABLE + "." + FRIENDSHIP_USERRECEIVER + ")");

        String where = FRIENDSHIP_USERSENDER + "=? OR " + FRIENDSHIP_USERRECEIVER + "=?";
        String[] whereArgs = { idUser, idUser };

        //richiesta al DB
        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

        while (cursor.moveToNext()) {
            String idCurrUser = cursor.getString(cursor.getColumnIndex(USERS_ID)); //TODO Probabile funzioni
            if(!idCurrUser.equals(idUser)) {  //se l'utente della riga selezionata è un amico
                User user = new User();

                user.id = idCurrUser;
                user.username = cursor.getString(USERS_USERNAME_COL);
                user.name = cursor.getString(USERS_NAME_COL);
                user.surname = cursor.getString(USERS_SURNAME_COL);
                user.gender = cursor.getString(USERS_GENDER_COL);
                user.profilepic = cursor.getString(USERS_PROFILEPIC_COL);

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                try {
                    user.birthdate = format.parse(cursor.getString(USERS_BIRTHDATE_COL));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                friends.add(user);
            }
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return friends;
    }

    public boolean areFriends(String idUser1, String idUser2) {
        String where = "(" + FRIENDSHIP_USERRECEIVER + "= ? AND " + FRIENDSHIP_USERSENDER + "=?) OR ("
                        + FRIENDSHIP_USERSENDER + "=? AND " + FRIENDSHIP_USERRECEIVER + "=?)";
        String[] whereArgs = { idUser1, idUser2, idUser1, idUser2 };

        openReadableDB();
        Cursor cursor = db.query(FRIENDSHIP_TABLE, null,
                where, whereArgs, null, null, null);

        boolean result;
        if(cursor.getCount() == 0){
            result = false;
        } else {
            result = true;
        }

        if (cursor != null)
            cursor.close();
        this.closeDB();

        return result;
    }


    /**
     * Ricerca utenti il cui nome, cognome o username contiene la stringa indicata
     *
     * @param searchText
     * @return
     */
    public ArrayList<User> searchUsers(String searchText) {
        ArrayList<User> friends = new ArrayList<>();
        openReadableDB();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(USERS_TABLE);
        String where = USERS_NAME + " LIKE '%?%' OR "
                        + USERS_SURNAME + " LIKE '%?%' OR "
                        + USERS_USERNAME + " LIKE '%?%'";
        String[] whereArgs = { searchText, searchText, searchText };

        //richiesta al DB
        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

        while (cursor.moveToNext()) {
            String idCurrUser = cursor.getString(cursor.getColumnIndex(USERS_ID)); //TODO Probabile funzioni
//            if(!idCurrUser.equals(idUser)) {  //se l'utente della riga selezionata è un amico
                User user = new User();

                user.id = idCurrUser;
                user.username = cursor.getString(USERS_USERNAME_COL);
                user.name = cursor.getString(USERS_NAME_COL);
                user.surname = cursor.getString(USERS_SURNAME_COL);
                user.gender = cursor.getString(USERS_GENDER_COL);
                user.profilepic = cursor.getString(USERS_PROFILEPIC_COL);

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                try {
                    user.birthdate = format.parse(cursor.getString(USERS_BIRTHDATE_COL));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                friends.add(user);
//            }
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return friends;
    }

//    public List getList(String name) {
//        String where = LIST_NAME + "= ?";
//        String[] whereArgs = { name };
//
//        openReadableDB();
//        Cursor cursor = db.query(LIST_TABLE, null,
//                where, whereArgs, null, null, null);
//        List list = null;
//        cursor.moveToFirst();
//        list = new List(cursor.getInt(LIST_ID_COL), cursor.getString(LIST_NAME_COL));
//        if (cursor != null)
//            cursor.close();
//        this.closeDB();
//
//        return list;
//    }

    /**
     * Restituisce i dati di un animale dato il suo id
     *
     * @param idAnimal id dell'animale
     * @return obj classe Animal dell'animale richiesto
     */
    public Animal getAnimal(String idAnimal) {
        String where = ANIMALS_ID + "= ?";
        String[] whereArgs = { idAnimal };

        //FIXME controlla idAnim null

        openReadableDB();
        Cursor cursor = db.query(ANIMALS_TABLE, null,
                where, whereArgs, null, null, null);

        if((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            Animal animal = new Animal();
            animal.id = cursor.getString(ANIMALS_ID_COL);
            animal.name = cursor.getString(ANIMALS_NAME_COL);
            animal.species = cursor.getString(ANIMALS_SPECIES_COL);
            animal.gender = cursor.getString(ANIMALS_GENDER_COL);
            animal.profilepic = cursor.getString(ANIMALS_PROFILEPIC_COL);

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
            try {
                animal.birthdate = format.parse(cursor.getString(ANIMALS_BIRTHDATE_COL));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            cursor.close();

            this.closeDB();

            return animal;
        } else {
            this.closeDB();
            return null;
        }

    }

//    public ArrayList<Animal> getAnimalsByOwner(String idOwner) {
//        ArrayList<Animal> animals = new ArrayList<>();
//        openReadableDB();
//
//        //Serve un join: usiamo QueryBuilder
//        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//
//        //indichiamo le tabelle su cui lavorare
//        qb.setTables(USERS_TABLE + ", " + USERSANIMALS_TABLE + ", " + ANIMALS_TABLE);
//
////        qb.setTables("(" + USERS_TABLE + " JOIN " + USERSANIMALS_TABLE +
////                " ON " + USERS_TABLE + "." + USERS_ID + "=" + USERSANIMALS_TABLE + "." + USERSANIMALS_IDUSER + ")"
////                + " JOIN " + ANIMALS_TABLE + " ON " + USERSANIMALS_TABLE + "." + USERSANIMALS_IDANIMAL + "=" + ANIMALS_TABLE + "." + ANIMALS_ID);
//
//        String where = USERS_TABLE + "." + USERS_ID + "=?";
////                +
////                " AND " + USERS_TABLE + "." + USERS_ID + "=" + USERSANIMALS_TABLE + "." + USERSANIMALS_IDUSER;
//// +
////                " AND " + USERSANIMALS_TABLE + "." + USERSANIMALS_IDANIMAL + "=" + ANIMALS_TABLE + "." + ANIMALS_ID;
//        String[] whereArgs = { idOwner };
////        String where = null;
////        String[] whereArgs = null;
//
//        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);
//
//        cursor.moveToFirst();
//        while (cursor.moveToNext()) {
//            Animal animal = new Animal();
//
//            //TODO fare il resto
//            animal.name = cursor.getString(cursor.getColumnIndex(ANIMALS_NAME));
//            animal.id = cursor.getString(cursor.getColumnIndex(ANIMALS_TABLE+"."+ANIMALS_ID)) + " " +
//                        cursor.getString(cursor.getColumnIndex(USERSANIMALS_TABLE+"."+USERSANIMALS_IDANIMAL)) + ", " +
//                        cursor.getString(cursor.getColumnIndex(USERSANIMALS_TABLE+"."+USERSANIMALS_IDUSER)) + " " +
//                        cursor.getString(cursor.getColumnIndex(USERS_TABLE+"."+USERS_ID));
//            for(int i=0; i<cursor.getColumnNames().length; i++){
//                animal.id += " " + cursor.getColumnNames()[i];
//            }
//
//            animals.add(animal);
//        }
//        if (cursor != null)
//            cursor.close();
//        closeDB();
//
//        return animals;
//    }


    public ArrayList<Animal> getAnimalsByOwner(String idOwner) {
        ArrayList<Animal> animals = new ArrayList<>();
        openReadableDB();

        //Serve un join: usiamo QueryBuilder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(USERS_TABLE + " JOIN " + USERSANIMALS_TABLE +
                " ON (" + USERS_TABLE + "." + USERS_ID + "=" + USERSANIMALS_TABLE + "." + USERSANIMALS_IDUSER + ")"
                + " JOIN " + ANIMALS_TABLE +
                " ON (" + USERSANIMALS_TABLE + "." + USERSANIMALS_IDANIMAL + "=" + ANIMALS_TABLE + "." + ANIMALS_ID + ")");

        String where = USERS_TABLE + "." + USERS_ID + "=?";
        String[] whereArgs = { idOwner };

        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

//        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            Animal animal = new Animal();

//            animal.id = cursor.getString(cursor.getColumnIndex(ANIMALS_TABLE+"."+ANIMALS_ID)) + " " +
//                    cursor.getString(cursor.getColumnIndex(USERSANIMALS_TABLE+"."+USERSANIMALS_IDANIMAL)) + ", " +
//                    cursor.getString(cursor.getColumnIndex(USERSANIMALS_TABLE+"."+USERSANIMALS_IDUSER)) + " " +
//                    cursor.getString(cursor.getColumnIndex(USERS_TABLE+"."+USERS_ID));
//            for(int i=0; i<cursor.getColumnNames().length; i++){
//                animal.id += " " + cursor.getColumnNames()[i];
//            }
            animal.name = cursor.getString(cursor.getColumnIndex(ANIMALS_NAME));
            animal.id = cursor.getString(cursor.getColumnIndex(ANIMALS_ID));
            animal.species = cursor.getString(cursor.getColumnIndex(ANIMALS_SPECIES));
            animal.gender = cursor.getString(cursor.getColumnIndex(ANIMALS_GENDER));
            animal.profilepic = cursor.getString(cursor.getColumnIndex(ANIMALS_PROFILEPIC));

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
            try {
                animal.birthdate = format.parse(cursor.getString(cursor.getColumnIndex(ANIMALS_BIRTHDATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            animals.add(animal);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return animals;
    }


    /**
     * Inserisce un animale nel DB. Inserisce il link tra user e animal.
     *
     * @param a animale da inserire
     * @param userId Id utente da collegare
     * @return Numero della riga inserita. <br>
     *         -1 in caso di errore nell'inserimento
     */
    public long insertAnimal(Animal a, String userId) {
        Log.d("MyPet", "Insert animal " + a.id + ", " + a.name);

        String where = ANIMALS_ID + "= ?";
        String[] whereArgs = { a.id };

        openReadableDB();
        Cursor cursor = db.query(ANIMALS_TABLE, null,
                where, whereArgs, null, null, null);

        int presence = cursor.getCount();
        cursor.close();
        this.closeDB();

        long rowIDAnim = -1;
        long rowIDUserAnim = -1;

        if(presence > 0) {   //animale già presente nel DB
//            rowIDAnim = updateAnimal(a);
            return presence;    //Altrimenti si rischia di cancellare dati inseriti.
                                // Usare updateAnimal per aggiornare i dati dell'animale
        } else {
            ContentValues cv = new ContentValues();
            cv.put(ANIMALS_ID, a.id);
            cv.put(ANIMALS_NAME, a.name);
            cv.put(ANIMALS_GENDER, a.gender);
            cv.put(ANIMALS_SPECIES, a.species);
            cv.put(ANIMALS_PROFILEPIC, a.profilepic);

            if(a.birthdate != null) {
                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                cv.put(ANIMALS_BIRTHDATE, format.format(a.birthdate));
            }
            this.openWriteableDB();
            rowIDAnim = db.insert(ANIMALS_TABLE, null, cv);

            if(userId != null && rowIDAnim != -1){    //Se l'insert va a buon fine
                cv = new ContentValues();
                cv.put(USERSANIMALS_IDANIMAL, a.id);
                cv.put(USERSANIMALS_IDUSER, userId);
                rowIDUserAnim = db.insert(USERSANIMALS_TABLE, null, cv);
            }
        }

        this.closeDB();

        return rowIDAnim;
    }


    public int updateAnimal(Animal a) {
        ContentValues cv = new ContentValues();
        cv.put(ANIMALS_ID, a.id);
        cv.put(ANIMALS_NAME, a.name);
        cv.put(ANIMALS_GENDER, a.gender);
        cv.put(ANIMALS_SPECIES, a.species);
        cv.put(ANIMALS_PROFILEPIC, a.profilepic);

        SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
        cv.put(ANIMALS_BIRTHDATE, format.format(a.birthdate));

        String where = ANIMALS_ID + "= ?";
        String[] whereArgs = { String.valueOf(a.id) };

        this.openWriteableDB();
        int rowCount = db.update(ANIMALS_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

//    public ArrayList<Task> getTasks(String listName) {
//        String where =
//                TASK_LIST_ID + "= ? AND " +
//                        TASK_HIDDEN + "!='1'";
//        int listID = getList(listName).getId();
//        String[] whereArgs = { Integer.toString(listID) };
//
//        this.openReadableDB();
//        Cursor cursor = db.query(TASK_TABLE, null,
//                where, whereArgs,
//                null, null, null);
//        ArrayList<Task> tasks = new ArrayList<Task>();
//        while (cursor.moveToNext()) {
//            tasks.add(getTaskFromCursor(cursor));
//        }
//        if (cursor != null)
//            cursor.close();
//        this.closeDB();
//
//        return tasks;
//    }
//
//    public Task getTask(int id) {
//        String where = TASK_ID + "= ?";
//        String[] whereArgs = { Integer.toString(id) };
//
//        this.openReadableDB();
//        Cursor cursor = db.query(TASK_TABLE,
//                null, where, whereArgs, null, null, null);
//        cursor.moveToFirst();
//        Task task = getTaskFromCursor(cursor);
//        if (cursor != null)
//            cursor.close();
//        this.closeDB();
//
//        return task;
//    }
//
//    private static Task getTaskFromCursor(Cursor cursor) {
//        if (cursor == null || cursor.getCount() == 0){
//            return null;
//        }
//        else {
//            try {
//                Task task = new Task(
//                        cursor.getInt(TASK_ID_COL),
//                        cursor.getInt(TASK_LIST_ID_COL),
//                        cursor.getString(TASK_NAME_COL),
//                        cursor.getString(TASK_NOTES_COL),
//                        cursor.getString(TASK_COMPLETED_COL),
//                        cursor.getString(TASK_HIDDEN_COL));
//                return task;
//            }
//            catch(Exception e) {
//                return null;
//            }
//        }
//    }
//
//    public long insertTask(Task task) {
//        ContentValues cv = new ContentValues();
//        cv.put(TASK_LIST_ID, task.getListId());
//        cv.put(TASK_NAME, task.getName());
//        cv.put(TASK_NOTES, task.getNotes());
//        cv.put(TASK_COMPLETED, task.getCompletedDate());
//        cv.put(TASK_HIDDEN, task.getHidden());
//
//        this.openWriteableDB();
//        long rowID = db.insert(TASK_TABLE, null, cv);
//        this.closeDB();
//
//        return rowID;
//    }

    public ArrayList<Post> getPostsByAuthor(String idAuthor, GoogleApiHelper gApiHelper) {
        ArrayList<Post> posts = new ArrayList<>();
        openReadableDB();

        String where = POSTS_IDAUTHOR + "=?";
        String[] whereArgs = { idAuthor };

        Cursor cursor = db.query(POSTS_TABLE,
                null, where, whereArgs, null, null, null);
        while (cursor.moveToNext()) {
            Post post = new Post();

            post.id = cursor.getString(POSTS_ID_COL);
            post.text = cursor.getString(POSTS_TEXT_COL);
            post.idauthor = cursor.getString(POSTS_IDAUTHOR_COL);
            post.setPlace(gApiHelper, cursor.getString(POSTS_PLACE_COL));

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd H:m:s");
            try {
                post.date = format.parse(cursor.getString(POSTS_DATE_COL));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            User author = getUser(post.idauthor);
            post.nameauthor = author.username;
            post.picauthor = author.profilepic;

            post.picture = cursor.getString(POSTS_PICTURE_COL);

            post.users = getTaggedUsersByPost(post.id);
            post.animals = getTaggedAnimalsByPost(post.id);

            posts.add(post);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return posts;
    }

    public ArrayList<Post> getPostsByAnimal(String idAnimal, GoogleApiHelper gApiHelper) {
        ArrayList<Post> posts = new ArrayList<>();
        openReadableDB();

        //Serve un join: usiamo QueryBuilder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(POSTS_TABLE + " JOIN " + POSTANIMALS_TABLE +
                " ON (" + POSTS_TABLE+"."+POSTS_ID + "=" + POSTANIMALS_TABLE+"."+POSTANIMALS_IDPOST + ")" +
                " JOIN " + ANIMALS_TABLE +
                " ON (" + POSTANIMALS_TABLE+"."+POSTANIMALS_IDANIMAL + "=" + ANIMALS_TABLE+"."+ANIMALS_ID + ")");

        String where = ANIMALS_TABLE+"."+ANIMALS_ID + "=?";
        String[] whereArgs = { idAnimal };

        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

        while (cursor.moveToNext()) {
            Post post = new Post();

            //TODO fare il resto
            post.id = cursor.getString(cursor.getColumnIndex(POSTS_ID));
            post.text = cursor.getString(cursor.getColumnIndex(POSTS_TEXT));
            post.idauthor = cursor.getString(cursor.getColumnIndex(POSTS_IDAUTHOR));
            post.setPlace(gApiHelper, cursor.getString(cursor.getColumnIndex(POSTS_PLACE)));

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd H:m:s");
            try {
                post.date = format.parse(cursor.getString(cursor.getColumnIndex(POSTS_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            User author = getUser(post.idauthor);
            post.nameauthor = author.username;
            post.picauthor = author.profilepic;

            post.picture = cursor.getString(cursor.getColumnIndex(POSTS_PICTURE));

            post.users = getTaggedUsersByPost(post.id);
            post.animals = getTaggedAnimalsByPost(post.id);

            posts.add(post);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return posts;
    }

    /**
     * Recupera tutti i post in cui l'utente figura taggato o è autore
     *
     * @param idUser id dell'utente da ricercare nel DB
     * @return ArrayList dei post cercati
     */
    public ArrayList<Post> getPostsByUser(String idUser, GoogleApiHelper gApiHelper) {
        ArrayList<Post> posts = new ArrayList<>();
        openReadableDB();

        //########### POST in cui si è TAGGATI ##############à
        //Serve un join: usiamo QueryBuilder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(POSTS_TABLE + " JOIN " + POSTUSERS_TABLE +
                " ON (" + POSTS_TABLE+"."+POSTS_ID + "=" + POSTUSERS_TABLE+"."+POSTUSERS_IDPOST + ")" +
                " JOIN " + USERS_TABLE +
                " ON (" + POSTUSERS_TABLE+"."+POSTUSERS_IDUSER + "=" + USERS_TABLE+"."+USERS_ID + ")");

        String where = USERS_TABLE+"."+USERS_ID + "=?";
        String[] whereArgs = { idUser };

        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

        while (cursor.moveToNext()) {
            Post post = new Post();

            post.id = cursor.getString(cursor.getColumnIndex(POSTS_ID));
            post.text = cursor.getString(cursor.getColumnIndex(POSTS_TEXT));
            post.idauthor = cursor.getString(cursor.getColumnIndex(POSTS_IDAUTHOR));
            post.picture = cursor.getString(cursor.getColumnIndex(POSTS_PICTURE));
            post.setPlace(gApiHelper, cursor.getString(cursor.getColumnIndex(POSTS_PLACE)));

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd H:m:s");
            try {
                post.date = format.parse(cursor.getString(cursor.getColumnIndex(POSTS_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            User author = getUser(post.idauthor);
            post.picauthor = author.profilepic;
            post.nameauthor = author.username;

            post.users = getTaggedUsersByPost(post.id);
            post.animals = getTaggedAnimalsByPost(post.id);

            posts.add(post);
        }

        if (cursor != null)
            cursor.close();
        closeDB();

        //########## POST in cui si è AUTORE ##########
        ArrayList<Post> authPost = getPostsByAuthor(idUser, gApiHelper);

        posts.addAll(authPost);

        //TODO ordinare per data
        //Collections.sort(posts);

        return posts;
    }

    public long insertPost(Post p) {
        Log.d("MyPet", "Insert post " + p.id + ", by " + p.idauthor);
        //Controlla se l'utente è già nel DB
        String where = POSTS_ID + "= ?";
        String[] whereArgs = { p.id };

        openReadableDB();
        Cursor cursor = db.query(POSTS_TABLE, null,
                where, whereArgs, null, null, null);

        int presence = cursor.getCount();
        cursor.close();
        this.closeDB();

        long rowID = presence;
        if(presence == 0) {        //Se il post non è nel DB
            ContentValues cv = new ContentValues();
            cv.put(POSTS_ID, p.id);
            cv.put(POSTS_IDAUTHOR, p.idauthor);
            cv.put(POSTS_TEXT, p.text);
//            cv.put(POSTS_PLACE_LAT, p.place.latitude);
//            cv.put(POSTS_PLACE_LON, p.place.longitude);
            if(p.placeId != null) {
                cv.put(POSTS_PLACE, p.placeId);
            }
            cv.put(POSTS_PICTURE, p.picture);

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd HH:mm:ss");
            cv.put(POSTS_DATE, format.format(p.date));

            //Aggiunta di animali e utenti taggati
            for(User u : p.users){
                insertUser(u);
                linkUserPost(u.id, p.id);
            }
            for(Animal a : p.animals){
                insertAnimal(a, null);
                linkAnimalPost(a.id, p.id);
            }

            this.openWriteableDB();
            rowID = db.insert(POSTS_TABLE, null, cv);
            this.closeDB();
        }

        return rowID;
    }

    public long linkUserPost(String idUser, String idPost) {
        ContentValues cv = new ContentValues();
        cv.put(POSTUSERS_IDUSER, idUser);
        cv.put(POSTUSERS_IDPOST, idPost);

        this.openWriteableDB();
        long rowID = db.insert(POSTUSERS_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public ArrayList<User> getTaggedUsersByPost(String idPost){
        ArrayList<User> users = new ArrayList<User>();
        openReadableDB();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(POSTUSERS_TABLE + " JOIN " + USERS_TABLE +
                " ON (" + POSTUSERS_TABLE+"."+POSTUSERS_IDUSER + "=" + USERS_TABLE+"."+USERS_ID + ")");

        String where = POSTUSERS_TABLE+"."+POSTUSERS_IDPOST + "=?";
        String[] whereArgs = { idPost };

        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

        while (cursor.moveToNext()) {
            User u = new User();

            u.id = cursor.getString(cursor.getColumnIndex(USERS_ID));
            u.username = cursor.getString(cursor.getColumnIndex(USERS_USERNAME));
            u.profilepic = cursor.getString(cursor.getColumnIndex(USERS_PROFILEPIC));

            users.add(u);
        }

        return users;
    }

    public long linkAnimalPost(String idAnimal, String idPost) {
        ContentValues cv = new ContentValues();
        cv.put(POSTANIMALS_IDANIMAL, idAnimal);
        cv.put(POSTUSERS_IDPOST, idPost);

        this.openWriteableDB();
        long rowID = db.insert(POSTANIMALS_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public ArrayList<Animal> getTaggedAnimalsByPost(String idPost){
        ArrayList<Animal> animals = new ArrayList<>();
        openReadableDB();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(POSTANIMALS_TABLE + " JOIN " + ANIMALS_TABLE +
                " ON (" + POSTANIMALS_TABLE+"."+POSTANIMALS_IDANIMAL + "=" + ANIMALS_TABLE+"."+ANIMALS_ID + ")");

        String where = POSTANIMALS_TABLE+"."+POSTANIMALS_IDPOST + "=?";
        String[] whereArgs = { idPost };

        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

        if((cursor != null) && (cursor.getCount() > 0)){
            while (cursor.moveToNext()) {
                Animal a = new Animal();

                a.id = cursor.getString(cursor.getColumnIndex(ANIMALS_ID));
                a.name = cursor.getString(cursor.getColumnIndex(ANIMALS_NAME));
                a.profilepic = cursor.getString(cursor.getColumnIndex(ANIMALS_PROFILEPIC));

                animals.add(a);
            }

            this.closeDB();
            return animals;
        } else {
            this.closeDB();
            return null;
        }


    }

    public long insertReminder(Reminder r) {
        Log.d("MyPet", "Insert reminder " + r.id + ", " + r.eventname);

        String where = REMINDERS_IDREMINDER + "= ?";
        String[] whereArgs = {r.id};

        openReadableDB();
        Cursor cursor = db.query(REMINDERS_TABLE, null,
                where, whereArgs, null, null, null);

        int presence = cursor.getCount();
        cursor.close();
        this.closeDB();

        long rowIDRem = -1;

        if (presence > 0) {   //animale già presente nel DB
//            rowIDAnim = updateAnimal(a);
            return presence;    //Altrimenti si rischia di cancellare dati inseriti.
        } else {
            ContentValues cv = new ContentValues();
            cv.put(REMINDERS_IDREMINDER, r.id);
            cv.put(REMINDERS_IDUSER, r.iduser);
            cv.put(REMINDERS_IDANIM, r.idanim);
            cv.put(REMINDERS_EVENTNAME, r.eventname);
            cv.put(REMINDERS_EVENTPLACE, r.eventplace);
            cv.put(REMINDERS_EVENTTIME, r.eventtime);

            this.openWriteableDB();
            rowIDRem = db.insert(REMINDERS_TABLE, null, cv);

        }

        return rowIDRem;
    }

    public ArrayList<Reminder> getRemindersByUser(String idUser) {
        ArrayList<Reminder> reminders = new ArrayList<>();
        openReadableDB();

        //Serve un join: usiamo QueryBuilder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(REMINDERS_TABLE + " JOIN " + ANIMALS_TABLE +
                " ON (" + REMINDERS_TABLE+"."+REMINDERS_IDANIM + "=" + ANIMALS_TABLE+"."+ANIMALS_ID + ")");

        String where = REMINDERS_IDUSER + "=?";
        String[] whereArgs = { idUser };

        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

        if((cursor != null) && (cursor.getCount() > 0)){
            while (cursor.moveToNext()) {
                Reminder r = new Reminder();

                r.id = cursor.getString(cursor.getColumnIndex(REMINDERS_IDREMINDER));
                r.idanim = cursor.getString(cursor.getColumnIndex(REMINDERS_IDANIM));
                r.eventname = cursor.getString(cursor.getColumnIndex(REMINDERS_EVENTNAME));
                r.eventplace = cursor.getString(cursor.getColumnIndex(REMINDERS_EVENTPLACE));
                r.eventtime = cursor.getString(cursor.getColumnIndex(REMINDERS_EVENTTIME));
                r.animname = cursor.getString(cursor.getColumnIndex(ANIMALS_NAME));
                r.animpic = cursor.getString(cursor.getColumnIndex(ANIMALS_PROFILEPIC));

                reminders.add(r);
            }

            cursor.close();
            closeDB();

            return reminders;
        } else {    //Se non ci sono risultati
            closeDB();
            return null;
        }

    }

//    public int updateTask(Task task) {
//        ContentValues cv = new ContentValues();
//        cv.put(TASK_LIST_ID, task.getListId());
//        cv.put(TASK_NAME, task.getName());
//        cv.put(TASK_NOTES, task.getNotes());
//        cv.put(TASK_COMPLETED, task.getCompletedDate());
//        cv.put(TASK_HIDDEN, task.getHidden());
//
//        String where = TASK_ID + "= ?";
//        String[] whereArgs = { String.valueOf(task.getId()) };
//
//        this.openWriteableDB();
//        int rowCount = db.update(TASK_TABLE, cv, where, whereArgs);
//        this.closeDB();
//
//        return rowCount;
//    }
//
//    public int deleteTask(long id) {
//        String where = TASK_ID + "= ?";
//        String[] whereArgs = { String.valueOf(id) };
//
//        this.openWriteableDB();
//        int rowCount = db.delete(TASK_TABLE, where, whereArgs);
//        this.closeDB();
//
//        return rowCount;
//    }

    public ArrayList<String> debugUsersAnimals() {
        ArrayList<String> strs = new ArrayList<>();
        openReadableDB();

        //Serve un join: usiamo QueryBuilder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //indichiamo le tabelle su cui lavorare
        qb.setTables(USERSANIMALS_TABLE);
        String where = null;
        String[] whereArgs = null;

        Cursor cursor = qb.query(db, null, where, whereArgs, null, null, null);

//        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String str ="";
            for(int i=0; i<cursor.getColumnNames().length; i++){
                str += cursor.getColumnNames()[i] + ":" + cursor.getString(cursor.getColumnIndex(cursor.getColumnNames()[i])) + " ";
            }
            strs.add(str);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return strs;
    }


}
