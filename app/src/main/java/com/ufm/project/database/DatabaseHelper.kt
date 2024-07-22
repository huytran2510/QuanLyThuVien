package com.ufm.project.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "library.db"
        const val DATABASE_VERSION = 2
        const val TABLE_NAME = "Book"
        const val COLUMN_TITLE = "title"
        const val COLUMN_SUBTITLE = "subtitle"
        const val COLUMN_AUTHORS = "authors"
        const val COLUMN_PUBLISHER = "publisher"
        const val COLUMN_PUBLISHED_DATE = "published_date"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_PAGE_COUNT = "page_count"
        const val COLUMN_THUMBNAIL = "thumbnail"
        const val COLUMN_PREVIEW_LINK = "preview_link"
        const val COLUMN_INFO_LINK = "info_link"


        const val TABLE_TK_NAME = "taikhoan"
        const val COLUMN_TK_ID = "matk"
        const val COLUMN_TK_USERNAME = "username"
        const val COLUMN_TK_PASSWORD = "password"
        const val COLUMN_TK_LOAITK = "loaitk"

        const val TABLE_TT_NAME = "thuthu"
        const val COLUMN_TT_ID = "mathuthu"
        const val COLUMN_TT_NAME = "hoten"
        const val COLUMN_TT_ADDRESS = "diachi"
        const val COLUMN_TT_NGAYSINH = "ngaysinh"
        const val COLUMN_TT_GIOITINH = "gioitinh"
        const val COLUMN_TT_DIENTHOAI = "dienthoai"

        const val TABLE_DG_NAME = "docgia"
        const val COLUMN_DG_ID = "madocgia"
        const val COLUMN_DG_NAME = "hoten"
        const val COLUMN_DG_ADDRESS = "diachi"
        const val COLUMN_DG_NGAYSINH = "ngaysinh"
        const val COLUMN_DG_GIOITINH = "gioitinh"
        const val COLUMN_DG_DIENTHOAI = "dienthoai"

        const val TABLE_NCC_NAME = "ncc"
        const val COLUMN_NCC_ID = "mancc"
        const val COLUMN_NCC_NAME = "tenncc"
        const val COLUMN_NCC_DIACHI = "diachi"
        const val COLUMN_NCC_DIENTHOAI = "dienthoai"

        const val TABLE_PM_NAME = "phieumuon"
        const val COLUMN_PM_ID = "maphieumuon"
        const val COLUMN_PM_NGAYMUON = "ngaymuon"
        const val COLUMN_PM_NGAYTRA = "ngaytra"
        const val COLUMN_PM_MAKH = "makh"
        const val COLUMN_PM_MATHU = "mathu"
        const val COLUMN_PM_SOLUONG = "soluong"
        const val COLUMN_PM_GHICHU = "ghichu"

        const val TABLE_CTPM_NAME = "chitietphieumuon"
        const val COLUMN_CTPM_MASACH = "masach"
        const val COLUMN_CTPM_MAPM = "mapm"

        const val TABLE_BOOK_NAME = "sach"
        const val COLUMN_BOOK_MASACH = "masach"
        const val COLUMN_BOOK_TENSACH = "tensach"
        const val COLUMN_BOOK_PHUDE = "phude"
        const val COLUMN_BOOK_MOTA = "mota"
        const val COLUMN_BOOK_TACGIA = "tacgia"
        const val COLUMN_BOOK_NXB = "nxb"
        const val COLUMN_BOOK_NGAYNHAP = "ngaynhap"
        const val COLUMN_BOOK_SOLUONG = "soluong"
        const val COLUMN_BOOK_ANH = "anh"

        const val TABLE_PT_NAME = "phieutra"
        const val COLUMN_PT_MAPT = "mapt"
        const val COLUMN_PT_NGAYTRA = "ngaytra"
        const val COLUMN_PT_SOLUONGMUON = "soluongmuon"
        const val COLUMN_PT_SOLUONGTRA = "soluongtra"

        const val TABLE_CTPT_NAME = "chitietphieutra"
        const val COLUMN_CTPT_MASACH = "masach"
        const val COLUMN_CTPT_MAPT = "mapt"

        const val TABLE_TL_NAME = "theloai"
        const val COLUMN_MALOAI = "maloai"
        const val COLUMN_TENLOAI = "tenloai"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_SUBTITLE TEXT," +
                "$COLUMN_AUTHORS TEXT," +
                "$COLUMN_PUBLISHER TEXT," +
                "$COLUMN_PUBLISHED_DATE TEXT," +
                "$COLUMN_DESCRIPTION TEXT," +
                "$COLUMN_PAGE_COUNT INTEGER," +
                "$COLUMN_THUMBNAIL TEXT," +
                "$COLUMN_PREVIEW_LINK TEXT," +
                "$COLUMN_INFO_LINK TEXT)"


        val createTableTK = "CREATE TABLE $TABLE_TK_NAME (" +
                "$COLUMN_TK_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TK_USERNAME VARCHAR," +
                "$COLUMN_TK_PASSWORD VARCHAR," +
                "$COLUMN_TK_LOAITK VARCHAR )";

        val createTableTT = "CREATE TABLE $TABLE_TT_NAME (" +
                "$COLUMN_TT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TT_NAME VARCHAR," +
                "$COLUMN_TT_ADDRESS VARCHAR," +
                "$COLUMN_TT_DIENTHOAI VARCHAR," +
                "$COLUMN_TT_GIOITINH VARCHAR," +
                "$COLUMN_TT_NGAYSINH DATE ," +
                "$COLUMN_TK_ID INTEGER ," +
                "FOREIGN KEY ($COLUMN_TK_ID) REFERENCES $TABLE_TK_NAME($COLUMN_TK_ID) ON DELETE CASCADE )"

        val createTableDG = "CREATE TABLE $TABLE_DG_NAME (" +
                "$COLUMN_DG_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_DG_NAME VARCHAR," +
                "$COLUMN_DG_ADDRESS VARCHAR," +
                "$COLUMN_DG_NGAYSINH DATE," +
                "$COLUMN_DG_DIENTHOAI VARCHAR," +
                "$COLUMN_DG_GIOITINH VARCHAR," +
                "$COLUMN_TK_ID INTEGER ," +
                "FOREIGN KEY ($COLUMN_TK_ID) REFERENCES $TABLE_TK_NAME($COLUMN_TK_ID) ON DELETE CASCADE )"

        val createTableNCC = "CREATE TABLE $TABLE_NCC_NAME (" +
                "$COLUMN_NCC_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NCC_NAME VARCHAR," +
                "$COLUMN_NCC_DIACHI VARCHAR," +
                "$COLUMN_NCC_DIENTHOAI VARCHAR)"

        val createTablePM = "CREATE TABLE $TABLE_PM_NAME (" +
                "$COLUMN_PM_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PM_NGAYMUON DATE," +
                "$COLUMN_PM_NGAYTRA DATE," +
                "$COLUMN_PM_MAKH INTEGER," +
                "$COLUMN_PM_MATHU INTEGER," +
                "$COLUMN_PM_SOLUONG INTEGER," +
                "$COLUMN_PM_GHICHU VARCHAR ," +
                "FOREIGN KEY ($COLUMN_PM_MAKH) REFERENCES $TABLE_DG_NAME($COLUMN_DG_ID) ON DELETE CASCADE ," +
                "FOREIGN KEY ($COLUMN_PM_MATHU) REFERENCES $TABLE_TT_NAME($COLUMN_TT_ID) ON DELETE CASCADE )"

        val createTableCTPM = "CREATE TABLE $TABLE_CTPM_NAME (" +
                "$COLUMN_CTPM_MASACH INTEGER," +
                "$COLUMN_CTPM_MAPM INTEGER," +
                "FOREIGN KEY ($COLUMN_CTPM_MASACH) REFERENCES $TABLE_BOOK_NAME($COLUMN_BOOK_MASACH) ON DELETE CASCADE," +
                "FOREIGN KEY ($COLUMN_CTPM_MAPM) REFERENCES $TABLE_PM_NAME($COLUMN_PM_ID) ON DELETE CASCADE)"

        val createTableTL = "CREATE TABLE $TABLE_TL_NAME (" +
                "$COLUMN_MALOAI INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TENLOAI VARCHAR)"

        val createTablePT = "CREATE TABLE $TABLE_PT_NAME (" +
                "$COLUMN_PT_MAPT INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PT_NGAYTRA DATE," +
                "$COLUMN_PT_SOLUONGMUON INTEGER," +
                "$COLUMN_PT_SOLUONGTRA INTEGER," +
                "$COLUMN_PM_ID INTEGER ," +
                "FOREIGN KEY ($COLUMN_PM_ID) REFERENCES $TABLE_PM_NAME($COLUMN_PM_ID) ON DELETE CASCADE )"

        val createTableCTPT = "CREATE TABLE $TABLE_CTPT_NAME (" +
                "$COLUMN_CTPT_MASACH INTEGER," +
                "$COLUMN_CTPT_MAPT INTEGER," +
                "FOREIGN KEY ($COLUMN_CTPT_MASACH) REFERENCES $TABLE_BOOK_NAME($COLUMN_BOOK_MASACH) ON DELETE CASCADE," +
                "FOREIGN KEY ($COLUMN_CTPT_MAPT) REFERENCES $TABLE_PT_NAME($COLUMN_CTPT_MAPT) ON DELETE CASCADE)"

        val createTableBook = "CREATE TABLE $TABLE_BOOK_NAME (" +
                "$COLUMN_BOOK_MASACH INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_BOOK_TENSACH VARCHAR, " +
                "$COLUMN_BOOK_PHUDE VARCHAR," +
                "$COLUMN_BOOK_MOTA VARCHAR," +
                "$COLUMN_BOOK_TACGIA VARCHAR," +
                "$COLUMN_BOOK_NXB VARCHAR," +
                "$COLUMN_BOOK_NGAYNHAP DATE," +
                "$COLUMN_BOOK_SOLUONG INTEGER," +
                "$COLUMN_BOOK_ANH VARCHAR, " +
                "$COLUMN_MALOAI INTEGER," +
                "$COLUMN_NCC_ID INTEGER," +
                "FOREIGN KEY ($COLUMN_MALOAI) REFERENCES $TABLE_TL_NAME($COLUMN_MALOAI) ON DELETE CASCADE," +
                "FOREIGN KEY ($COLUMN_NCC_ID) REFERENCES $TABLE_NCC_NAME($COLUMN_NCC_ID) ON DELETE CASCADE)"

        db.execSQL(createTableQuery)
        db.execSQL(createTableTK)
        db.execSQL(createTableTT)
        db.execSQL(createTableDG)
        db.execSQL(createTableNCC)
        db.execSQL(createTablePM)
        db.execSQL(createTableCTPM)
        db.execSQL(createTableTL)
        db.execSQL(createTablePT)
        db.execSQL(createTableCTPT)
        db.execSQL(createTableBook)
//        Toast.makeText(context, "Database created", Toast.LENGTH_SHORT).show()
        val insertTableTL = "INSERT INTO $TABLE_TL_NAME ($COLUMN_TENLOAI) VALUES\n" +
                "('Văn học Việt Nam'),\n" +
                "('Lập trình'),\n" +
                "('Khoa học viễn tưởng'),\n" +
                "('Tiểu thuyết lãng mạn'),\n" +
                "('Truyện trinh thám'),\n" +
                "('Truyện cổ tích'),\n" +
                "('Tâm lý - Kỹ năng sống'),\n" +
                "('Kinh tế - Quản trị'),\n" +
                "('Sách giáo khoa'),\n" +
                "('Sách thiếu nhi');"
        db.execSQL(insertTableTL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TK_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TT_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DG_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NCC_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PM_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CTPM_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TL_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PT_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CTPT_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOK_NAME")
        onCreate(db)
    }


}