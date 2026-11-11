# HerPathway

Aplikasi desktop mentoring (JavaFX + MySQL): mentee mencari mentor, mengirim lamaran,
lalu chat setelah diterima. Ada juga forum diskusi dan pustaka belajar yang isinya dibuat pengguna.

## Menjalankan

1. Buat database MySQL kosong bernama `herpath`. Tabel dibuat otomatis saat aplikasi pertama kali
   terhubung (lihat `SchemaMigrator` dan `src/main/resources/db/schema.sql`).
2. Salin `db.properties.example` ke `db.properties`, lalu isi kredensial MySQL.
   (`db.properties` di-ignore git. Bisa juga lewat env `HERPATH_DB_URL`, `HERPATH_DB_USER`, `HERPATH_DB_PASSWORD`.)
3. (Opsional, untuk chat live) jalankan server chat: `mvn exec:java`
4. Jalankan aplikasi: `mvn javafx:run`

Di VS Code: buka folder ini, Java extension akan membaca `pom.xml`. Gunakan launch config
**ChatServer** dan **HerPathway App**. Jalankan dari folder root ini, karena file upload
disimpan di `uploads/` relatif terhadap folder kerja.

## Alur aplikasi

1. **Register → Login.** Setelah login pertama, user mengisi form profil sesuai perannya, lalu masuk
   ke **Home** (dashboard). Password disimpan sebagai hash PBKDF2; akun lama (teks polos) otomatis
   dikonversi saat login berikutnya.
2. **Mentee:** Find a mentor → buka profil mentor → *Apply* (kuisioner). Satu lamaran per mentor.
   Status tiap lamaran (waiting / accepted / declined) tampil di Home.
3. **Mentor:** Mentee requests → *Review* → *Accept mentee* atau *Decline*. Mentee yang diterima
   ditugaskan ke mentor tersebut (`mentor_assignment`, satu mentor per mentee).
4. **Messages:** mentee hanya melihat mentornya, dan mentor hanya melihat mentee yang ia terima.
   Pesan disimpan di database; jika `ChatServer` berjalan, pesan juga muncul live.
5. **Forum:** semua peran bisa membuat diskusi (dengan kategori), membalas, memberi like, mencari,
   dan menghapus tulisan sendiri.
6. **Learning:** pustaka artikel, video, dan e-book. Mentor dan organisasi bisa mempublikasikan
   (artikel ditulis di aplikasi; video/e-book berupa tautan). Mentee hanya membaca.
7. **Organisasi:** punya Home, Forum, Learning, dan halaman edit profil organisasi.

## Struktur folder

```
.
├── pom.xml                  build Maven (JavaFX, MySQL connector)
├── db.properties.example    template kredensial database
├── uploads/                 file upload user saat runtime (di-ignore git)
└── src/main
    ├── java/com/herpathway
    │   ├── app/          Main (bootstrap JavaFX), Launcher (entry point classpath biasa)
    │   ├── config/       DatabaseConfig: baca kredensial dari env / db.properties
    │   ├── database/     DatabaseConnection (satu-satunya tempat membuka koneksi JDBC),
    │   │                 SchemaMigrator (buat tabel/kolom yang belum ada), StarterContent
    │   ├── dao/          semua SQL (UserDao, MenteeDao, MentorDao, OrganizationDao, QuestionnaireDao,
    │   │                 MentorshipDao, ChatDao, ForumDao, LearningDao, DashboardDao, UserDirectoryDao)
    │   ├── model/        objek data (MenteeProfile, MentorProfile, Questionnaire, ...)
    │   ├── navigation/   View (daftar layar + FXML) dan Navigator (pindah layar)
    │   ├── session/      SessionManager: user yang sedang login
    │   ├── chat/         ChatServer (socket server) dan ChatClient
    │   ├── util/         Alerts, Ui, Images, FileStorage, ImageCompressor, Passwords
    │   └── controller/   controller FXML: hanya logika UI, tanpa SQL
    └── resources
        ├── com/herpathway/view/   file FXML
        ├── css/                   stylesheet (app.css)
        ├── db/schema.sql          definisi tabel, dijalankan otomatis oleh SchemaMigrator
        └── image/                 gambar UI
```

Alur dependensi satu arah: `controller → dao → database → config`. Controller tidak boleh
menulis SQL atau membuka koneksi sendiri.

### Aturan singkat

- **Pindah layar**: `Navigator.goTo(View.X)` untuk mengganti seluruh jendela,
  `Navigator.show(View.X)` untuk mengganti isi content area di dalam shell (`Shell.fxml`),
  `Navigator.openWindow(View.X, "Judul", ...)` untuk jendela baru.
  Untuk mengirim data ke controller tujuan:
  `Navigator.<ProfilMentorController>show(View.MENTOR_PUBLIC_PROFILE, c -> c.setMentorData(m));`
- **Layar baru**: FXML di `resources/com/herpathway/view/`, controller di `controller/`,
  lalu tambahkan konstanta di `View`. Menu sidebar per peran diatur di `ShellController`.
- **Tabel/kolom baru**: tambahkan `CREATE TABLE IF NOT EXISTS` di `resources/db/schema.sql`; untuk
  kolom baru di tabel lama, tambahkan `addColumnIfMissing(...)` di `SchemaMigrator`.
- **Gambar di FXML**: simpan di `resources/image/` dan referensikan dengan
  `@../../../image/nama.png`. Jangan menunjuk ke Downloads/Pictures di laptop sendiri.
- **Styling**: semua gaya ada di `resources/css/app.css` (dipasang otomatis oleh Navigator ke setiap
  jendela dan dialog). Pakai style class yang sudah ada (`page`, `card`, `page-title`, `btn-primary`,
  `btn-secondary`, `badge`, ...) dan hindari `style="..."` inline.
- **Query baru**: tambahkan method di DAO yang sesuai, jangan di controller.
- **Pesan ke user**: `Alerts.info(...)` / `Alerts.error(...)`.
- **Upload file**: `FileStorage.choose(...)` lalu `FileStorage.store(...)`.
