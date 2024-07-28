package com.ufm.project.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "library.db"
        const val DATABASE_VERSION = 6
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
        const val COLUMN_PT_GHICHU = "ghichu"


        const val TABLE_CTPT_NAME = "chitietphieutra"
        const val COLUMN_CTPT_MASACH = "masach"
        const val COLUMN_CTPT_MAPT = "mapt"

        const val TABLE_TL_NAME = "theloai"
        const val COLUMN_MALOAI = "maloai"
        const val COLUMN_TENLOAI = "tenloai"

    }

    override fun onCreate(db: SQLiteDatabase?) {
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
                "$COLUMN_PM_ID VARCHAR PRIMARY KEY ," +
                "$COLUMN_PM_NGAYMUON DATE," +
                "$COLUMN_PM_NGAYTRA DATE," +
                "$COLUMN_DG_ID INTEGER," +
                "$COLUMN_PM_MATHU INTEGER," +
                "$COLUMN_PM_SOLUONG INTEGER," +
                "$COLUMN_PM_GHICHU VARCHAR ," +
                "FOREIGN KEY ($COLUMN_DG_ID) REFERENCES $TABLE_DG_NAME($COLUMN_DG_ID) ON DELETE CASCADE ," +
                "FOREIGN KEY ($COLUMN_PM_MATHU) REFERENCES $TABLE_TT_NAME($COLUMN_TT_ID) ON DELETE CASCADE )"

        val createTableCTPM = "CREATE TABLE $TABLE_CTPM_NAME (" +
                "$COLUMN_CTPM_MASACH INTEGER," +
                "$COLUMN_CTPM_MAPM VARCHAR," +
                "FOREIGN KEY ($COLUMN_CTPM_MASACH) REFERENCES $TABLE_BOOK_NAME($COLUMN_BOOK_MASACH) ON DELETE CASCADE," +
                "FOREIGN KEY ($COLUMN_CTPM_MAPM) REFERENCES $TABLE_PM_NAME($COLUMN_PM_ID) ON DELETE CASCADE)"

        val createTableTL = "CREATE TABLE $TABLE_TL_NAME (" +
                "$COLUMN_MALOAI INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TENLOAI VARCHAR)"

        val createTablePT = "CREATE TABLE $TABLE_PT_NAME (" +
                "$COLUMN_PT_MAPT VARCHAR PRIMARY KEY," +
                "$COLUMN_PT_NGAYTRA DATE," +
                "$COLUMN_PT_SOLUONGMUON INTEGER," +
                "$COLUMN_PT_SOLUONGTRA INTEGER," +
                "$COLUMN_PT_GHICHU VARCHAR," +
                "$COLUMN_PM_ID VARCHAR ," +
                "FOREIGN KEY ($COLUMN_PM_ID) REFERENCES $TABLE_PM_NAME($COLUMN_PM_ID) ON DELETE CASCADE )"

        val createTableCTPT = "CREATE TABLE $TABLE_CTPT_NAME (" +
                "$COLUMN_CTPT_MASACH INTEGER," +
                "$COLUMN_CTPT_MAPT VARCHAR," +
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

        db?.execSQL(createTableQuery)
        db?.execSQL(createTableTK)
        db?.execSQL(createTableTT)
        db?.execSQL(createTableDG)
        db?.execSQL(createTableNCC)
        db?.execSQL(createTablePM)
        db?.execSQL(createTableCTPM)
        db?.execSQL(createTableTL)
        db?.execSQL(createTablePT)
        db?.execSQL(createTableCTPT)
        db?.execSQL(createTableBook)

        //****************INSERT DATABASE***********************
        //INSERT THE LOAI
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
        db?.execSQL(insertTableTL)

        //INSERT TAI KHOAN
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('admin', '123456789', 'admin')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('tranhuy', '123456789', 'khachhang')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('huyhoang', '123456789', 'khachhang')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('trongkha', '123456789', 'khachhang')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('luongvy', '123456789', 'khachhang')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('xuantinh', '123456789', 'khachhang')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('manhmui', '123456789', 'khachhang')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('huycan', '123456789', 'khachhang')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('khanhnguyen', '123456789', 'khachhang')")
        db?.execSQL("INSERT INTO $TABLE_TK_NAME ($COLUMN_TK_USERNAME, $COLUMN_TK_PASSWORD, $COLUMN_TK_LOAITK) VALUES ('tranhoang', '123456789', 'khachhang');")

        //INSERT THU THU
        val insertTableTT = "INSERT INTO $TABLE_TT_NAME (" +
                "$COLUMN_TT_NAME," +
                "$COLUMN_TT_ADDRESS," +
                "$COLUMN_TT_DIENTHOAI," +
                "$COLUMN_TT_GIOITINH," +
                "$COLUMN_TT_NGAYSINH ," +
                "$COLUMN_TK_ID)" +
                "VALUES" +
                "('Nguyễn Văn An', '123 Đường Trần Hưng Đạo, Quận 1, TP.HCM', '1990-01-01', '0901234567', 'Nam', 1)"

        //INSERT DOC GIA
        val insertTableDG =
            "INSERT INTO $TABLE_DG_NAME ($COLUMN_DG_NAME, $COLUMN_DG_ADDRESS, $COLUMN_DG_NGAYSINH, $COLUMN_DG_DIENTHOAI, $COLUMN_DG_GIOITINH, $COLUMN_TK_ID)\n" +
                    "VALUES\n" +
                    "('Nguyễn Văn An', '123 Đường Trần Hưng Đạo, Quận 1, TP.HCM', '1990-01-01', '0901234567', 'Nam', 1),\n" +
                    "('Trần Thị Hoa', '456 Đường Nguyễn Huệ, Quận 2, TP.HCM', '1992-02-14', '0902345678', 'Nữ', 2),\n" +
                    "('Lê Văn CHí', '789 Đường Võ Văn Tần, Quận 3, TP.HCM', '1988-03-21', '0903456789', 'Nam', 3),\n" +
                    "('Hoàng Thị Dung', '101 Đường Bạch Đằng, Quận 4, TP.HCM', '1995-04-30', '0904567890', 'Nữ', 4),\n" +
                    "('Vũ Văn Nam', '202 Đường Nguyễn Chí Thanh, Quận 5, TP.HCM', '1989-05-15', '0905678901', 'Nam', 5),\n" +
                    "('Phan Thị Phương', '303 Đường Phan Văn Chí, Quận 6, TP.HCM', '1993-06-10', '0906789012', 'Nữ', 6),\n" +
                    "('Đăng Minh Giang', '404 Đường Nguyễn Công Trư, Quận 7, TP.HCM', '1991-07-25', '0907890123', 'Nam', 7),\n" +
                    "('Nguyễn Thị Hai', '505 Đường Võ Văn Công, Quận 8, TP.HCM', '1994-08-18', '0908901234', 'Nữ', 8),\n" +
                    "('Mai Văn Hải', '606 Đường Lê Văn Việt, Quận 9, TP.HCM', '1987-09-12', '0909012345', 'Nam', 9),\n" +
                    "('Lê Kim Hiền', '707 Đường 385, Quận 10, TP.HCM', '1996-10-05', '0900123456', 'Nữ', 10);"

        //INSERT NHA CUNG CAP
        val insertTableNCC = "INSERT INTO $TABLE_NCC_NAME (mancc, tenncc, diachi, dienthoai)\n" +
                "VALUES\n" +
                "  (1, 'Nhà sách Đường Sách TP.HCM', 'Đường Nguyễn Văn Cừ, Quận 1, TP.HCM', '(028) 3822 9782'),\n" +
                "  (2, 'Nhà Sách Fahasa', '248 Nguyễn Đình Chiểu, Quận 3, TP.HCM', '(028) 3823 0000'),\n" +
                "  (3, 'Nhà sách Kim Đồng', '65 Nguyễn Văn Cừ, Quận 1, TP.HCM', '(028) 3829 2222'),\n" +
                "  (4, 'Nhà sách Cá Chép', '125 Nguyễn Đình Chiểu, Quận 3, TP.HCM', '(028) 3820 7755'),\n" +
                "  (5, 'Nhà sách Phương Nam', '281 - 283 Nguyễn Đình Chiểu, Quận 3, TP.HCM', '(028) 3822 8181');\n"
        db?.execSQL(insertTableNCC)
        db?.execSQL(insertTableDG)
        val insertTableBook = "INSERT INTO $TABLE_BOOK_NAME ($COLUMN_BOOK_TENSACH, $COLUMN_BOOK_PHUDE, $COLUMN_BOOK_MOTA, $COLUMN_BOOK_TACGIA, $COLUMN_BOOK_NXB, $COLUMN_BOOK_NGAYNHAP, $COLUMN_BOOK_SOLUONG, $COLUMN_BOOK_ANH, $COLUMN_MALOAI, $COLUMN_NCC_ID )\n VALUES \n "  +
                "('Dế Mèn Phiêu Lưu Ký', 'Cuộc phiêu lưu kỳ thú của Dế Mèn trên cánh đồng xanh', 'Một câu chuyện đầy màu sắc và trí tưởng tượng của Dế Mèn, từ việc khám phá thế giới xung quanh đến những thử thách bất ngờ. Cuốn sách mở ra một thế giới tuổi thơ tươi đẹp với các bài học sâu sắc.', 'Tô Hoài', 'NXB Kim Đồng', '2023-01-15', 15, 'https://static.8cache.com/cover/o/eJzLyTDW1_VIzDROLfM3Noh31A8LM8zQLQlx8Uj11HeEgrw8V_0o5-Ck1IDyQEf3bP1iAwDLihCU/de-men-phieu-luu-ky.jpg',1, 1 ), \n" +
                "('Harry Potter và Hòn Đá Phù Thủy', 'Cuộc phiêu lưu ma thuật tại trường Hogwarts','Cuốn sách đưa bạn vào thế giới kỳ diệu của Harry Potter với những bí ẩn chưa được giải đáp và những cuộc chiến đấu ngoạn mục chống lại các thế lực đen tối. Một hành trình đầy ma thuật và phiêu lưu ở trường Hogwarts.', 'J.K. Rowling','NXB Thế Giới', '2023-02-20', 20,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaFrm74pTLUb7H9tmrDEpEkenXX9_--_Hcyw&s', 3, 2 ), \n " +
                "('Đắc Nhân Tâm','Quyển sách của mọi thời đại và một hiện tượng đáng kinh ngạc', 'Cuốn sách cung cấp những chiến lược hiệu quả để cải thiện kỹ năng giao tiếp và xây dựng mối quan hệ tích cực. Đây là một cẩm nang hữu ích cho việc tạo dựng các kết nối cá nhân và nghề nghiệp bền vững.', 'Dale Carnegie',  'NXB Thế Giới', '2023-03-05', 25, 'https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg', 3, 3 ), \n" +
                "('Bí Quyết Phát Triển Bản Thân', 'Những chiến lược để trở thành phiên bản tốt nhất của chính bạn', 'Cuốn sách này chia sẻ những phương pháp và chiến lược cụ thể để phát triển bản thân, từ việc xác định mục tiêu cá nhân đến cách duy trì động lực và đạt được thành công trong cuộc sống.', 'Stephen R. Covey', 'NXB Trẻ', '2023-04-10', 18,  'https://salt.tikicdn.com/cache/w300/ts/product/f3/3d/17/2911a6f69519986428ec4127043c6f41.jpg', 7, 4 ),\n" +
                "('Cuộc Đời Của Pi','Cuộc phiêu lưu kỳ diệu trên đại dương', 'Một câu chuyện sâu sắc về hành trình sống còn của Pi Patel, một cậu bé 16 tuổi cùng với con hổ Bengal Richard Parker trên một chiếc thuyền cứu sinh giữa đại dương. Cuốn sách là một cuộc phiêu lưu không thể quên.', 'Yann Martel',  'NXB Văn Học',  '2023-05-25', 12, 'https://upload.wikimedia.org/wikipedia/vi/9/96/Cuoc_doi_cua_Pi.jpg', 6, 5 ),\n" +
                "('Sapiens: Lược Sử Loài Người', 'Khám phá lịch sử phát triển của loài người từ thuở sơ khai đến hiện đại', 'Cuốn sách cung cấp một cái nhìn sâu sắc về sự phát triển của loài người từ thời kỳ săn bắn hái lượm cho đến nền văn minh hiện đại, khám phá những sự kiện và xu hướng chính đã hình thành xã hội hiện tại.', 'Yuval Noah Harari', 'NXB Lao Động', '2023-06-30',  10,  'https://bizweb.dktcdn.net/100/197/269/products/sapiens-luoc-su-ve-loai-nguoi-outline-5-7-2017-02.jpg?v=1520935327270',  3, 2 ), \n" +
                "('Làm Giàu Không Khó', 'Những phương pháp và chiến lược đơn giản để làm giàu','\"Thói quen giàu có - Thói quen thành công hàng ngày của người giàu\" của Bobby K. Munoz. Nó vạch ra những chiến lược và thói quen thực tế mà bất cứ ai cũng có thể áp dụng để đạt được thành công về mặt tài chính. Cuốn sách nhấn mạnh tầm quan trọng của tư duy, kỷ luật và hành động để xây dựng sự giàu có theo thời gian.' , 'Bobby K. Munoz', 'NXB Trẻ',  '2023-07-15',  22,  'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcREzvc-QYekjU-_G4QDZysk-FtqRzD4fj-I4g&s',  7,  4 ), \n" +
                "('Những Điều Kỳ Diệu Trong Cuộc Sống', 'Khám phá những điều tuyệt vời và ý nghĩa trong cuộc sống hàng ngày','Điều Kỳ Diệu Của Cuộc Sống Như những khoảng lặng cần thiết trong tiết tấu của cuộc sống, những câu chuyện trong cuốn sách này sẽ mang đến cho bạn cơ hội để suy ngẫm và rung động về những chuyện có thực, về những điều có thể nhỏ nhặt nhưng ý nghĩa với mỗi chúng ta. Những câu chuyện trong Tủ sách Sống đẹp là tình cảm, nỗi niềm tâm sự sẻ chia của đông đảo bạn bè trên thế giới, họ ở mọi tầng lớp trong xã hội nhưng có chung một mong muốn là đánh thức trái tim nhân hậu và tình yêu thương trong mỗi con người.', 'Osho',  'NXB Thế Giới',  '2023-08-20',  16, 'https://salt.tikicdn.com/cache/280x280/media/catalog/product/d/i/dieu-ky-dieu-cua-cuoc-song.jpg', 8,  3 ),\n" +
                "('Người Lạ Trong Gương','Khám phá bí ẩn và sự thật về người lạ trong gương', 'Người đàn ông cô độc. Toby Temple vừa là một siêu sao vừa là kẻ tàn nhẫn, vừa được tôn sùng như một vị thánh lại vừa bị quấy rầy bởi bao sự nghi vấn, ngờ vực. Người phụ nữ vỡ mộng. Jill Castle đến Hollywood để làm một ngôi sao- và phát hiện ra rằng nàng chỉ có thể có được nó bằng chính thân xác mình. Một thế giới của những kẻ lợi dụng lẫn nhau. Tất cả đều được tái hiện chân thực trong cuốn tiểu thuyết Người lạ trong gương. ', 'Gillian Flynn', 'NXB Văn Học',  '2023-09-10',  14, 'https://salt.tikicdn.com/cache/w1200/media/catalog/product/1/-/1-8.u2487.d20161229.t134124.815649.jpg', 9,  2 ),\n" +
                "('Dạy Con Làm Giàu', 'Hướng dẫn giáo dục tài chính cho trẻ em','Bộ sưu tập sách nói Dạy Con Làm Giàu bao gồm 13 cuốn sách về quản lý tài chính cá nhân của Robert T.Kiyosaki. Đây là bộ sách kinh điển về cách nhìn nhận đồng tiền, kiếm tiền và giúp chúng ta nâng cao năng lực tài chính. Với tầm ảnh hưởng rộng khắp, Dạy Con Làm Giàu xứng đáng là “giáo trình làm giàu” mà ai cũng nên đọc trong đời.',  'Robert T. Kiyosaki',  'NXB Trẻ',  '2023-10-01',  30, 'https://www.nxbtre.com.vn/Images/Book/copy_16_nxbtre_full_15412017_094159.jpg',10,  1 ), \n  " +
                "('Lập Trình C++', 'Học C++ cơ bản đến nâng cao', 'Sách hướng dẫn lập trình C++ từ cơ bản đến nâng cao', 'Trần Thông Quế', 'NXB Thông Tin Và Truyền Thông', '2023-02-02', 15, 'https://down-vn.img.susercontent.com/file/vn-11134201-7qukw-lfjgojqnj8g46d', 2, 2), \n" +
                "('Dune', 'Cuộc chiến sa mạc', 'Một câu chuyện khoa học viễn tưởng về hành tinh sa mạc', 'Frank Herbert', 'NXB Hội Nhà Văn', '2023-03-03', 8, 'https://vnmedia.vn/file/8a10a0d36ccebc89016ce0c6fa3e1b83/022024/2_20240229154447.jpg', 3, 3), \n" +
                "('Pride and Prejudice', 'Kiêu hãnh và định kiến', 'Một tiểu thuyết lãng mạn nổi tiếng của Jane Austen', 'Jane Austen', 'NXB Văn Học', '2023-04-04', 20, 'https://m.media-amazon.com/images/M/MV5BMTA1NDQ3NTcyOTNeQTJeQWpwZ15BbWU3MDA0MzA4MzE@._V1_.jpg', 4, 4), \n" +
                "('Sherlock Holmes', 'Thám tử lừng danh', 'Bộ truyện trinh thám kinh điển về thám tử Sherlock Holmes', 'Arthur Conan Doyle', 'NXB Kim Đồng', '2023-05-05', 12, 'https://product.hstatic.net/200000343865/product/sherlock-holmes---nhung-cuoc-phieu-luu-cua-sherlock-holmes_2af2e17154b54ce0be4875e51dec52d8_master.jpg', 5, 5), \n" +
                " ('Truyện cổ Grimm', 'Những câu chuyện cổ tích kinh điển', 'Tập hợp những câu chuyện cổ tích kinh điển của anh em Grimm', 'Anh em Grimm', 'NXB Văn Học', '2023-06-06', 10, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935236425872.jpg', 6, 5);"

        //INSERT PHIEU MUON
        val insertPM = """
            INSERT INTO $TABLE_PM_NAME (
                $COLUMN_PM_ID,
                $COLUMN_PM_NGAYMUON,
                $COLUMN_PM_NGAYTRA,
                $COLUMN_DG_ID,
                $COLUMN_PM_MATHU,
                $COLUMN_PM_SOLUONG,
                $COLUMN_PM_GHICHU
            ) VALUES 
            ('PM20240701', '2024-07-01', '2024-07-15', 8, 1, 3, 'First borrow'),
            ('PM20240705', '2024-07-05', '2024-07-20', 2, 1, 2, 'Second borrow'),
            ('PM20240710', '2024-07-10', '2024-07-25', 3, 1, 4, 'Third borrow'),
            ('PM20240715', '2024-07-15', '2024-07-30', 4, 1, 1, 'Fourth borrow'),
            ('PM20240720', '2024-07-20', '2024-08-05', 5, 1, 5, 'Fifth borrow'),
            ('PM20240725', '2024-07-25', '2024-08-10', 6, 1, 3, 'Sixth borrow'),
            ('PM20240730', '2024-07-30', '2024-08-15', 7, 1, 2, 'Seventh borrow'),
            ('PM20240801', '2024-08-01', '2024-08-16', 8, 1, 4, 'Eighth borrow'),
            ('PM20240805', '2024-08-05', '2024-08-20', 9, 1, 1, 'Ninth borrow'),
            ('PM20240810', '2024-08-10', '2024-08-25', 10, 1, 5, 'Tenth borrow'),
            ('PM20240815', '2024-08-15', '2024-08-30', 1, 1, 3, 'Eleventh borrow'),
            ('PM20240820', '2024-08-20', '2024-09-05', 2, 1, 2, 'Twelfth borrow'),
            ('PM20240825', '2024-08-25', '2024-09-10', 3, 1, 4, 'Thirteenth borrow'),
            ('PM20240830', '2024-08-30', '2024-09-15', 4, 1, 1, 'Fourteenth borrow'),
            ('PM20240901', '2024-09-01', '2024-09-16', 5, 1, 5, 'Fifteenth borrow');
        """

        // Insert data into CHITIETPHIEUMUON table
        val insertCTPM = "INSERT INTO $TABLE_CTPM_NAME ($COLUMN_CTPM_MASACH, $COLUMN_CTPM_MAPM) VALUES " +
                "(1, 'PM20240701'), (3, 'PM20240705')," +
                "(2, 'PM20240701'), (4, 'PM20240705')," +
                "(3, 'PM20240701'), (5, 'PM20240705')," +
                "(4, 'PM20240701'), (6, 'PM20240705')," +
                "(5, 'PM20240701'), (7, 'PM20240705')," +
                "(6, 'PM20240701'), (8, 'PM20240705')," +
                "(7, 'PM20240701'), (9, 'PM20240705')," +
                "(8, 'PM20240701'), (10, 'PM20240705')," +
                "(9, 'PM20240701'), (11, 'PM20240705')," +
                "(10, 'PM20240701'), (12, 'PM20240705')," +
                "(11, 'PM20240701'), (13, 'PM20240705')," +
                "(12, 'PM20240701'), (14, 'PM20240705')," +
                "(13, 'PM20240701'), (15, 'PM20240705');"

        // Insert data into PHIEUTRA table
        val insertPT = """
            INSERT INTO $TABLE_PT_NAME (
                $COLUMN_PT_MAPT,
                $COLUMN_PT_NGAYTRA,
                $COLUMN_PT_SOLUONGMUON,
                $COLUMN_PT_SOLUONGTRA,
                $COLUMN_PT_GHICHU,
                $COLUMN_PM_ID
            ) VALUES 
            ('PT20240701', '2024-07-15', 3, 3, 'First return', 'PM20240701'),
            ('PT20240705', '2024-07-20', 2, 2, 'Second return', 'PM20240705'),
            ('PT20240702', '2024-07-16', 4, 4, 'Third return', 'PM20240701'),
            ('PT20240706', '2024-07-21', 3, 3, 'Fourth return', 'PM20240705'),
            ('PT20240703', '2024-07-17', 5, 5, 'Fifth return', 'PM20240701'),
            ('PT20240707', '2024-07-22', 4, 4, 'Sixth return', 'PM20240705'),
            ('PT20240704', '2024-07-18', 6, 6, 'Seventh return', 'PM20240701'),
            ('PT20240708', '2024-07-23', 5, 5, 'Eighth return', 'PM20240705'),
            ('PT20240709', '2024-07-24', 7, 7, 'Ninth return', 'PM20240701'),
            ('PT20240710', '2024-07-25', 6, 6, 'Tenth return', 'PM20240705'),
            ('PT20240711', '2024-07-26', 8, 8, 'Eleventh return', 'PM20240701'),
            ('PT20240712', '2024-07-27', 7, 7, 'Twelfth return', 'PM20240705'),
            ('PT20240713', '2024-07-28', 9, 9, 'Thirteenth return', 'PM20240701'),
            ('PT20240714', '2024-07-29', 8, 8, 'Fourteenth return', 'PM20240705'),
            ('PT20240715', '2024-07-30', 10, 10, 'Fifteenth return', 'PM20240701');
        """

        // Insert data into CHITIETPHIEUTRA table
        val insertCTPT = "INSERT INTO $TABLE_CTPT_NAME ($COLUMN_CTPT_MASACH, $COLUMN_CTPT_MAPT) VALUES " +
                "(1, 'PT20240701'), (2, 'PT20240705')," +
                "(2, 'PT20240702'), (3, 'PT20240706')," +
                "(3, 'PT20240703'), (4, 'PT20240707')," +
                "(4, 'PT20240704'), (5, 'PT20240708')," +
                "(5, 'PT20240709'), (6, 'PT20240710')," +
                "(6, 'PT20240711'), (7, 'PT20240712')," +
                "(7, 'PT20240713'), (8, 'PT20240714')," +
                "(8, 'PT20240715'), (9, 'PT20240701')," +
                "(9, 'PT20240705'), (10, 'PT20240702')," +
                "(10, 'PT20240706'), (11, 'PT20240703')," +
                "(11, 'PT20240707'), (12, 'PT20240704')," +
                "(12, 'PT20240708'), (13, 'PT20240709')," +
                "(13, 'PT20240710'), (14, 'PT20240711')," +
                "(14, 'PT20240712'), (15, 'PT20240713')," +
                "(15, 'PT20240714'), (1, 'PT20240715');"

        db?.execSQL(insertTableTL)
        db?.execSQL(insertTableNCC)
        db?.execSQL(insertTableDG)
        db?.execSQL(insertTableTT)
        db?.execSQL(insertTableBook)
        db?.execSQL(insertPM)
        db?.execSQL(insertCTPM)
        db?.execSQL(insertPT)
        db?.execSQL(insertCTPT)

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