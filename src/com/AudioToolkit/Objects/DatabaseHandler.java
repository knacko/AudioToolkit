package com.AudioToolkit.Objects;

import java.util.Arrays;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHandler extends SQLiteAssetHelper  {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name + Loc
	//private static String DB_PATH = Environment.getExternalStorageDirectory().toString() + "/AudioToolkit/Database/";
	//private static String DB_PATH = "/data/data/com.AudioToolkitPro/databases/";
	private static final String DB_NAME = "AudioToolkitDB.db";

	// Contacts table name
	private static final String TABLE_VEHICLES = "vehicles";
	private static final String TABLE_SUBWOOFERS = "speakers";
	private static final String TABLE_ENCLOSURES = "enclosures";

	// Contacts Table Columns names for vehicles table
	private static final String KEY_ID = "id";
	private static final String KEY_MAKE = "make";
	private static final String KEY_MODEL = "model";
	private static final String KEY_YEAR = "year";
	private static final String KEY_BODY = "body";
	private static final String KEY_TRIM = "trim";
	private static final String KEY_SPEAKERS = "speakers";

	// Contacts Table Columns names for subwoofer table
	private static final String KEY_SUBID = "id";
	private static final String KEY_SUBNAME = "name";
	private static final String KEY_QTS = "qts";
	private static final String KEY_QES = "qes";
	private static final String KEY_PEMAX = "pemax";
	private static final String KEY_VAS = "vas";
	private static final String KEY_FS = "fs";
	private static final String KEY_XMAX = "xmax";
	private static final String KEY_SD = "sd";

	// Contacts Table Columns names for enclosure table
	private static final String KEY_ENCID = "id";
	private static final String KEY_ENCNAME = "name";
	private static final String KEY_TYPE = "type"; //0 for sealed, 1 for ported
	private static final String KEY_SIZE = "dim"; // "X,X,X" in order shown on screen
	private static final String KEY_SHAPE = "shape"; //0 for rect, 1 for wedge, 2 for cylinder
	private static final String KEY_FB = "portfreq"; //set to 0 for no port
	private static final String KEY_PORTAREA = "portarea"; //set to 0 for no port
	private static final String KEY_THICKNESS = "thickness";
	private static final String KEY_VB = "volume";
	private static final String KEY_NUMPORTS = "numports";
	private static final String KEY_PORTRATIO = "portratio";
	private static final String KEY_PORTLENGTH = "portlength";


	private boolean successfulStart = true;

	private final Context myContext;

	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		myContext = context;


		//checkDataBase();

		/*
		if (!checkDataBase()) {
			System.out.println("No database, copying");
			try {
				copyDataBase();
			} catch (IOException e) {
				// TO//DO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Copying complete");
		}*/

	}

	public boolean successfullyStarted() {

		return successfulStart;

	}

	// Creating Tables
	//@Override
	//public void onCreate(SQLiteDatabase db) {


	/*
		//Use for the creation of a new database
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_VEHICLES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," +  KEY_MAKE + " TEXT," + KEY_MODEL + " TEXT,"
				+ KEY_YEAR + " TEXT," + KEY_BODY + " TEXT," + KEY_TRIM + " TEXT," + KEY_SPEAKERS + " TEXT" +")";
		System.out.println("*******" + CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_CONTACTS_TABLE);
	 */
	//}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLES);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	void addVehicle(Vehicle vehicle) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, vehicle.getId()); 
		values.put(KEY_MAKE, vehicle.getMake()); 
		values.put(KEY_MODEL, vehicle.getModel()); 
		values.put(KEY_YEAR, vehicle.getYear());
		values.put(KEY_BODY, vehicle.getBody()); 
		values.put(KEY_TRIM, vehicle.getTrim()); 
		values.put(KEY_SPEAKERS, vehicle.getSpeakers());

		// Inserting Row
		db.insert(TABLE_VEHICLES, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	Vehicle getVehicle(int id) {

		SQLiteDatabase db = this.getReadableDatabase();


		Cursor cursor = db.query(TABLE_VEHICLES, new String[] { KEY_ID,
				KEY_MAKE, KEY_MODEL, KEY_YEAR, KEY_BODY, KEY_TRIM, KEY_SPEAKERS}, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);

		cursor.moveToFirst();

		Vehicle vehicle = new Vehicle(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
				cursor.getString(5), cursor.getString(6));
		// return contact
		return vehicle;
	}

	public String[] getMakes() {

		String s = "SELECT " + KEY_MAKE + " FROM " + TABLE_VEHICLES + " GROUP BY " + KEY_MAKE;



		return getSelectorArray(s, KEY_MAKE);

	}

	public String[] getModels(String amake) {

		String s = "SELECT " + KEY_MODEL + " FROM " + TABLE_VEHICLES + " WHERE " + KEY_MAKE + "='" + amake + "' GROUP BY " + KEY_MODEL;

		return getSelectorArray(s, KEY_MODEL);

	}

	public String[] getYear(String amake, String amodel) {

		String s = "SELECT " + KEY_YEAR + " FROM " + TABLE_VEHICLES + " WHERE " + KEY_MAKE + "='" + amake + "' AND " + KEY_MODEL +"='" + amodel +"' GROUP BY " + KEY_YEAR;

		return getSelectorArray(s, KEY_YEAR);

	}

	public String[] getBody(String amake, String amodel, String ayear) {

		String s = "SELECT " + KEY_BODY + " FROM " + TABLE_VEHICLES + " WHERE " + KEY_MAKE + "='" + amake + "' AND " 
				+ KEY_MODEL +"='" + amodel + "' AND " 
				+ KEY_YEAR +"='" + ayear +"' GROUP BY " + KEY_BODY;

		return getSelectorArray(s, KEY_BODY);

	}

	public String[] getTrim(String amake, String amodel, String ayear, String abody) {

		String s = "SELECT " + KEY_TRIM + " FROM " + TABLE_VEHICLES + " WHERE " + KEY_MAKE + "='" + amake + "' AND " 
				+ KEY_MODEL + "='" + amodel + "' AND "
				+ KEY_BODY + "='" + abody + "' AND " 
				+ KEY_YEAR + "='" + ayear +"' GROUP BY " + KEY_TRIM;

		return getSelectorArray(s, KEY_TRIM);

	}

	public String[] getSpeakers(String amake, String amodel, String ayear, String abody, String atrim) {

		String s = "SELECT " + KEY_SPEAKERS + " FROM " + TABLE_VEHICLES + " WHERE " + KEY_MAKE + "='" + amake + "' AND " 
				+ KEY_MODEL + "='" + amodel + "' AND "
				+ KEY_BODY + "='" + abody + "' AND " 		
				+ KEY_TRIM + "='" + atrim + "' AND " 
				+ KEY_YEAR + "='" + ayear +"' GROUP BY " + KEY_SPEAKERS;

		return getSelectorArray(s, KEY_SPEAKERS);

	}


	/*-------------------------------------------------------
	 * For the subwoofer table
	 */
	public String[] getSubwooferNames() {

		String s = "SELECT * FROM " + TABLE_SUBWOOFERS;

		return getSelectorArray(s, KEY_SUBNAME);

	}
	
	public Subwoofer[] getSubwoofers() {
		

		String[] names = getSubwooferNames();

		Subwoofer[] subwoofers = new Subwoofer[names.length];

		for (int i = 0; i < names.length; i++) {
			subwoofers[i] = getSubwoofer(names[i]);
		}

		return subwoofers;
		
		
	}
	

	public Subwoofer getSubwoofer(int id) {

		String s = "SELECT * FROM " + TABLE_SUBWOOFERS + " WHERE " + KEY_SUBID + "='" + id + "'";

		Log.d("getSub()","Sub ID: " + id);

		return getSubwooferHelper(s);


	}

	public Subwoofer getSubwoofer(String subname) {

		String s = "SELECT * FROM " + TABLE_SUBWOOFERS + " WHERE " + KEY_SUBNAME + "='" + subname + "'";

		Log.d("getSub()","Sub name: " + subname);

		return getSubwooferHelper(s);

	}    

	private Subwoofer getSubwooferHelper(String s) {

		SQLiteDatabase db = this.getReadableDatabase();//SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

		Cursor cursor = db.rawQuery(s,null);

		Log.d("getSubHelper()","subs: " + Arrays.toString(cursor.getColumnNames()));

		if (cursor.getCount() == 0)  return null;

		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndex(KEY_SUBNAME));
		double Qes = cursor.getDouble(cursor.getColumnIndex(KEY_QES));
		int id = cursor.getInt(cursor.getColumnIndex(KEY_SUBID));
		double Qts = cursor.getDouble(cursor.getColumnIndex(KEY_QTS));
		double Vas = cursor.getDouble(cursor.getColumnIndex(KEY_VAS));
		double Fs = cursor.getDouble(cursor.getColumnIndex(KEY_FS));
		double Xmax = cursor.getDouble(cursor.getColumnIndex(KEY_XMAX));
		double Pemax = cursor.getDouble(cursor.getColumnIndex(KEY_PEMAX));
		double Sd = cursor.getDouble(cursor.getColumnIndex(KEY_SD));

		db.close();

		return new Subwoofer(id, name, Qes, Qts, Vas, Fs, Pemax, Xmax, Sd);
	}

	public void deleteSubwoofer(Subwoofer sub) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_SUBWOOFERS, KEY_SUBID + " = ?",
				new String[] { String.valueOf(sub.getId()) });
		Log.d("deleteSub","sub " + sub.getName() + " (" + sub.getId()+ ") deleted");

		db.close();
	}

	public void addSubwoofer(Subwoofer sub) {
		if (sub.getId() != -1) { //change to edit sub if the sub already has an id
			Log.d("addsub","sub already exists, switching to update");
			updateSubwoofer(sub);
			return;
		}
		
		Log.d("addsub","sub doesn't exist, adding to database");

		ContentValues values = makeSubContentValues(sub);		

		// Inserting Row
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(TABLE_SUBWOOFERS, null, values);
		db.close(); // Closing database connection
		
		printSubwooferList();
	}

	public void updateSubwoofer(Subwoofer sub) {

		if (sub.getId() == -1) {
			Log.d("Database hander","Subwoofer cannot be updated, it's not in database. Will add instead.");
			addSubwoofer(sub);
		}

		ContentValues values = makeSubContentValues(sub);

		// updating row
		SQLiteDatabase db = this.getWritableDatabase();
		db.update(TABLE_SUBWOOFERS, values, KEY_SUBID + " = ?",
				new String[] { String.valueOf(sub.getId()) });
		db.close();
		return;
	}

	private ContentValues makeSubContentValues(Subwoofer sub) {

		ContentValues values = new ContentValues();

		sub.setId(sub.getId() == -1 ? getNextSubId() : sub.getId());

		//if (sub.getId() != -1) values.put(KEY_SUBID, sub.getId());

		values.put(KEY_SUBID, sub.getId());
		values.put(KEY_SUBNAME, sub.getName());
		values.put(KEY_QTS, sub.getQts());
		values.put(KEY_QES, sub.getQes());
		values.put(KEY_VAS, sub.getVas());
		values.put(KEY_PEMAX, sub.getPemax());
		values.put(KEY_XMAX, sub.getXmax());
		values.put(KEY_SD, sub.getSd());
		values.put(KEY_FS, sub.getFs());

		Log.d("makeSubContentValues()","Sub made/updated");
		Log.d("makeSubContentValues()","Id: " + sub.getId());
		Log.d("makeSubContentValues()","Name: " + sub.getName());
		Log.d("makeSubContentValues()","Vas: " + sub.getVas());
		Log.d("makeSubContentValues()","Qes: " + sub.getQes());
		Log.d("makeSubContentValues()","Qts: " + sub.getQts());
		Log.d("makeSubContentValues()","Fs: " + sub.getFs());
		Log.d("makeSubContentValues()","Sd: " + sub.getSd());
		Log.d("makeSubContentValues()","Pemax: " + sub.getPemax());
		Log.d("makeSubContentValues()","Xmax: " + sub.getXmax());

		return values;

	}

	private int getNextSubId() {

		String s = "SELECT " + KEY_SUBID + " FROM " + TABLE_SUBWOOFERS + " ORDER BY " + KEY_SUBID + " DESC LIMIT 1";

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery(s,null);


		int result;
		
		if (cursor.moveToFirst())
			result = 1+cursor.getInt(cursor.getColumnIndex(KEY_SUBID));
		else
			result = 1;
		
		
		db.close();
		
		return result;
	}


	private void printSubwooferList() {
		
		Subwoofer[] subwoofers = getSubwoofers();
		
		for (Subwoofer s:subwoofers) {
			
			System.out.println(s.toString());
			
		}
	}

	public int countSubwoofers() {
		String s = "SELECT * FROM " + TABLE_SUBWOOFERS;

		int count = countEntries(s);

		Log.d("getSubwooferCount()", count + " subwoofers counted");

		return count;
	}

	public boolean checkDupSubName(String name) {

		String s = "SELECT * FROM " + TABLE_SUBWOOFERS + " WHERE " + KEY_SUBNAME + "='" + name + "'";

		boolean check = checkDuplicates(s);

		Log.d("checkDupSubName()",name + (check ? " already exists." : " doesn't exist yet"));

		return check;

	}

	/************************************************8
	 * For the enclosure table
	 * 
	 */

	public String[] getEnclosureNames() {

		String s = "SELECT * FROM " + TABLE_ENCLOSURES;

		return getSelectorArray(s, KEY_ENCNAME);

	}

	public Enclosure[] getEnclosures() {

		String[] names = getEnclosureNames();

		Enclosure[] enclosures = new Enclosure[names.length];

		for (int i = 0; i < names.length; i++) {
			enclosures[i] = getEnclosure(names[i]);
		}

		return enclosures;
	}

	public Enclosure getEnclosure(int encid) {

		String s = "SELECT * FROM " + TABLE_ENCLOSURES + " WHERE " + KEY_ENCID + "='" + encid + "'";

		Log.d("getEnc(id)","Enc " + encid);

		return getEnclosureHelper(s);

	}

	public Enclosure getEnclosure(String encname) {

		//String s = "SELECT " + KEY_SUBID + " FROM " + TABLE_SUBWOOFERS + " WHERE " + KEY_SUBNAME + "='" + subname + "'";
		String s = "SELECT * FROM " + TABLE_ENCLOSURES + " WHERE " + KEY_ENCNAME + "='" + encname + "'";

		Log.d("getEnc(encname)","Enc " + encname);

		return getEnclosureHelper(s);

	}    

	private Enclosure getEnclosureHelper(String s) {

		SQLiteDatabase db = this.getReadableDatabase();//SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

		Cursor cursor = db.rawQuery(s,null);

		Log.d("getEncHelper()","Enc : " + Arrays.toString(cursor.getColumnNames()));

		if (cursor.getCount() == 0)  return null;

		cursor.moveToFirst();

		String name = cursor.getString(cursor.getColumnIndex(KEY_ENCNAME));
		int id = cursor.getInt(cursor.getColumnIndex(KEY_ENCID));
		int type = cursor.getInt(cursor.getColumnIndex(KEY_TYPE));
		int shape = cursor.getInt(cursor.getColumnIndex(KEY_SHAPE));
		double Vb = cursor.getDouble(cursor.getColumnIndex(KEY_VB));
		double thickness = cursor.getDouble(cursor.getColumnIndex(KEY_THICKNESS));
		String size = cursor.getString(cursor.getColumnIndex(KEY_SIZE));
		double Fb = cursor.getDouble(cursor.getColumnIndex(KEY_FB));
		double portarea = cursor.getDouble(cursor.getColumnIndex(KEY_PORTAREA));
		int numports = cursor.getInt(cursor.getColumnIndex(KEY_NUMPORTS));
		double portratio = cursor.getDouble(cursor.getColumnIndex(KEY_PORTRATIO));
		double portlength = cursor.getDouble(cursor.getColumnIndex(KEY_PORTLENGTH));

		db.close();

		return new Enclosure (id, name, type, shape, Vb, thickness, size, Fb,
				portarea, numports, portlength, portratio) ;

	}

	public void deleteEnclosure(Enclosure enc) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_ENCLOSURES, KEY_ENCID + " = ?",
				new String[] { String.valueOf(enc.getId()) });
		Log.d("deleteSub","sub " + enc.getName() + " deleted");

		db.close();
	}

	public void addEnclosure(Enclosure enc) {

		if (enc.getId() != -1) { //change to edit sub if the sub already has an id
			Log.d("addsub","sub already exists, switching to update");
			updateEnclosure(enc);
			return;
		}

		Log.d("addsub","sub doesn't exist, adding to database");

		ContentValues values = makeEncContentValues(enc);

		// Inserting Row
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(TABLE_ENCLOSURES, null, values);
		db.close(); // Closing database connection
		
		printEnclosureList();
	}

	public void updateEnclosure(Enclosure enc) {

		if (enc.getId() == -1) {
			Log.d("Database hander","Enclosure cannot be updated, it's not in database. Will add instead.");
			addEnclosure(enc);
		}

		ContentValues values = makeEncContentValues(enc);

		// updating row
		SQLiteDatabase db = this.getWritableDatabase();
		db.update(TABLE_ENCLOSURES, values, KEY_ENCID + " = ?",
				new String[] { String.valueOf(enc.getId()) });
		db.close();

	}

	private ContentValues makeEncContentValues(Enclosure enc) {

		ContentValues values = new ContentValues();

		int id = (enc.getId() == -1 ? getNextEncId() : enc.getId()); 

		enc.setId(id);
		
		values.put(KEY_ENCID, enc.getId());		
		values.put(KEY_ENCNAME, enc.getName());
		values.put(KEY_TYPE, enc.getType());
		values.put(KEY_SHAPE, enc.getShape());
		values.put(KEY_VB, enc.getVb());
		values.put(KEY_THICKNESS, enc.getThickness());
		values.put(KEY_SIZE, enc.getSize());
		values.put(KEY_FB, enc.getFb());
		values.put(KEY_PORTAREA, enc.getPortarea());
		values.put(KEY_NUMPORTS, enc.getNumports());
		values.put(KEY_PORTRATIO, enc.getPortratio());	

		Log.d("makeEncContentValues()","Enc made/updated");
		Log.d("makeEncContentValues()","Id; " + enc.getId());
		Log.d("makeEncContentValues()","Name; " + enc.getName());
		Log.d("makeEncContentValues()","Shape; " + enc.getShape());
		Log.d("makeEncContentValues()","Vb: " + enc.getVb());
		Log.d("makeEncContentValues()","Thickness: " + enc.getThickness());
		Log.d("makeEncContentValues()","Size: " + enc.getSize());
		Log.d("makeEncContentValues()","Fb: " + enc.getFb());
		Log.d("makeEncContentValues()","Portarea: " + enc.getPortarea());
		Log.d("makeEncContentValues()","Numports: " + enc.getNumports());
		Log.d("makeEncContentValues()","Port ratio: " + enc.getPortratio());

		return values;
	}

	private int getNextEncId() {

		String s = "SELECT " + KEY_ENCID + " FROM " + TABLE_ENCLOSURES + " ORDER BY " + KEY_ENCID + " DESC LIMIT 1";

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery(s,null);

		int result;
		
		if (cursor.moveToFirst())
			result = 1+cursor.getInt(cursor.getColumnIndex(KEY_ENCID));
		else
			result = 1;		
		
		db.close();
		
		return result;
	}

	public int countEnclosures() {

		String s = "SELECT * FROM " + TABLE_ENCLOSURES;

		int count = countEntries(s);

		Log.d("getEnclosureCount()",count + " enclosures counted");

		return count;
	}

	public boolean checkDupEncName(String name) {

		String s = "SELECT * FROM " + TABLE_ENCLOSURES + " WHERE " + KEY_ENCNAME + "='" + name + "'";

		boolean check = checkDuplicates(s);

		Log.d("checkDupEncName()",name + (check ? " already exists." : " doesn't exist yet"));

		return check;

	}

	private boolean checkDuplicates(String s) {

		return (countEntries(s) > 0) ? true : false;

	}

	private int countEntries(String s) {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(s, null);

		int count  = cursor.getCount();

		db.close();

		return count;
	}

	private void printEnclosureList() {
		
		Enclosure[] enclosures = getEnclosures();
		
		for (Enclosure e:enclosures) {
			
			System.out.println(e.toString());
			
		}
	}
	
	
	private String[] getSelectorArray(String request, String key) {

		SQLiteDatabase db = this.getReadableDatabase();//SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

		Cursor cursor = db.rawQuery(request,null);

		if (cursor == null) return null;

		String[] array = new String[cursor.getCount()];
		int i = 0;
		while(cursor.moveToNext()){
			String uname = cursor.getString(cursor.getColumnIndex(key));
			array[i] = uname;
			i++;
		}

		db.close();

		return array;

	}

	
	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	/*
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		Log.d("DatabaseHandler.checkDatabase(): ", "Checking database");

		try{

			checkDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

			if(checkDB == null){ 
				Log.e("DatabaseHandler.checkDatabase(): ", "Database copied, but not able to access");
				successfulStart = false;
				return false;
			}

			Log.d("DatabaseHandler.checkDatabase(): ", "Database is in place");
			checkDB.close();

			return true;

		} catch (SQLiteException e){

			Log.d("DatabaseHandler.checkDatabase(): ", "Database not found, copying");

			if (!copyDataBase()) {
				successfulStart = false;    
				Log.e("DatabaseHandler.checkDatabase(): ", "Database not copied");
				return false;
			}

			Log.d("DatabaseHandler.checkDatabase(): ", "Database successfully copied");

			return checkDataBase();
		}    	    	    	
	}
*/
	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	/*
	private boolean copyDataBase() { 

		try {
			//Open  db as the input stream
			InputStream myInput = myContext.getAssets().open(DB_NAME);
			Log.d("DatabaseHandler.copyDatabase()","InputStream opened");

			// Path to the new empty db
			File dir = new File(DB_PATH);
			dir.mkdirs();
			Log.d("DatabaseHandler.copyDatabase()","Directory created");

			File file = new File(DB_PATH, DB_NAME);
			file.createNewFile();
			Log.d("DatabaseHandler.copyDatabase()","File created");

			//Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(file);
			Log.d("DatabaseHandler.copyDatabase()","OutputStream created");

			//transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
			}

			Log.d("DatabaseHandler.copyDatabase()","File is written");
			//Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();

			return true;

		} catch (IOException e1) {
			Log.d("DatabaseHandler.copyDatabase(): ", "Error in copying the database.");  
			return false;
		}

	}   */ 
}
