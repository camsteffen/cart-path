package isu.cartpath;

import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class GroceryKnowledge extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "cartpath.db";
	private static final int DATABASE_VERSION = 1;

	public GroceryKnowledge(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}