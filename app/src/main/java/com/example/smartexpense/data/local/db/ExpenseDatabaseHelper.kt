package com.example.smartexpense.data.local.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.smartexpense.data.model.Expense

private const val DB_NAME    = "expenses.db"
private const val DB_VERSION = 1

private const val TABLE_EXPENSES   = "expenses"
private const val COL_ID           = "id"
private const val COL_AMOUNT       = "amount"
private const val COL_DESC         = "description"
private const val COL_CATEGORY     = "category"
private const val COL_TIMESTAMP    = "timestamp"

class ExpenseDatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createStmt = """
          CREATE TABLE $TABLE_EXPENSES (
            $COL_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_AMOUNT    REAL    NOT NULL,
            $COL_DESC      TEXT    NOT NULL,
            $COL_CATEGORY  TEXT    NOT NULL,
            $COL_TIMESTAMP INTEGER NOT NULL
          )
        """.trimIndent()
        db.execSQL(createStmt)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    /** Insert an Expense; returns the new row's ID */
    fun insertExpense(expense: Expense): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_AMOUNT,    expense.amount)
            put(COL_DESC,      expense.description)
            put(COL_CATEGORY,  expense.category)
            put(COL_TIMESTAMP, expense.timestamp)
        }
        return db.insert(TABLE_EXPENSES, null, values)
    }

    fun deleteExpense(id: Long): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_EXPENSES,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteExpenseByTimestamp(timestamp: Long): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_EXPENSES,
            "$COL_TIMESTAMP = ?",
            arrayOf(timestamp.toString())
        )
    }

    /** Query all expenses, ordered by timestamp descending */
    fun getAllExpenses(): List<Expense> {
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(COL_ID, COL_AMOUNT, COL_DESC, COL_CATEGORY, COL_TIMESTAMP),
            null, null, null, null,
            "$COL_TIMESTAMP DESC"
        )

        val list = mutableListOf<Expense>()
        cursor.use {
            while (it.moveToNext()) {
                val id        = it.getLong(it.getColumnIndexOrThrow(COL_ID))
                val amount    = it.getDouble(it.getColumnIndexOrThrow(COL_AMOUNT))
                val desc      = it.getString(it.getColumnIndexOrThrow(COL_DESC))
                val category  = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(COL_TIMESTAMP))
                list += Expense(id, amount, desc, category, timestamp)
            }
        }
        return list
    }
}
