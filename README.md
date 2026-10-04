# YinYangSolver

Solver untuk puzzle **Yin-Yang** ([puzzle-yin-yang.com](https://www.puzzle-yin-yang.com/)) menggunakan **Algoritma Genetika (Genetic Algorithm)** yang ditulis dalam Java. Proyek ini dibuat untuk tugas besar mata kuliah Kecerdasan Buatan (Tubes AI).

## Daftar Isi

- [Tentang Puzzle](#tentang-puzzle)
- [Cara Kerja Solver](#cara-kerja-solver)
- [Struktur Proyek](#struktur-proyek)
- [Persyaratan](#persyaratan)
- [Cara Menjalankan](#cara-menjalankan)
- [Format File](#format-file)
- [Contoh](#contoh)
- [Keterbatasan](#keterbatasan)
- [Pengembangan Selanjutnya](#pengembangan-selanjutnya)

## Tentang Puzzle

Yin-Yang dimainkan pada grid persegi. Sebagian sel sudah terisi warna **hitam** atau **putih** (clue), sisanya kosong. Tujuannya adalah mewarnai semua sel sehingga memenuhi tiga aturan berikut:

1. Semua sel hitam harus terhubung secara ortogonal (atas, bawah, kiri, kanan) dalam satu kelompok.
2. Semua sel putih harus terhubung secara ortogonal dalam satu kelompok.
3. Tidak boleh ada area 2x2 yang berwarna sama.

Sumber aturan: [puzzle-yin-yang.com](https://www.puzzle-yin-yang.com/). Situs tersebut menyediakan ukuran 6x6 sampai 25x25 dengan tingkat Easy, Normal, dan Hard.

## Cara Kerja Solver

Puzzle dipandang sebagai masalah optimasi. Setiap kandidat solusi adalah sebuah papan berisi 0 dan 1, lalu Algoritma Genetika mencari kandidat dengan nilai penalti **0**.

### Representasi

| Nilai | Arti |
|-------|------|
| `1`   | Sel hitam |
| `0`   | Sel putih |
| `-1`  | Sel kosong (hanya pada papan input) |

Satu **individu** (`YinYangCell`) adalah papan `n x n` berisi 0/1. Satu **populasi** (`YinyangPopulation`) adalah kumpulan individu.

### Fungsi Fitness (Heuristik)

Semakin kecil nilai heuristik, semakin baik individu. Nilai 0 berarti solusi sempurna. Penalti dihitung dari:

| Pelanggaran | Penalti |
|-------------|---------|
| Selisih jumlah sel hitam dan putih | `abs(hitam - putih)` |
| Setiap blok 2x2 satu warna | `+10` per blok |
| Sel hitam tidak terhubung | `+50` |
| Sel putih tidak terhubung | `+50` |

Konektivitas diperiksa dengan **DFS** dari satu sel awal, lalu memastikan semua sel berwarna sama ikut terkunjungi.

### Alur Algoritma

```
1. Bangkitkan populasi acak sebesar populationSize
2. Hitung heuristik tiap individu, urutkan dari terkecil
3. Selama generasi < maxGenerations DAN heuristik terbaik > 0:
     a. Elitisme   : salin sebagian individu terbaik ke generasi baru
     b. Seleksi    : pilih 2 induk dengan roulette wheel
     c. Crossover  : hasilkan 2 anak
     d. Mutasi     : anak dimutasi dengan peluang mutationRate
     e. Ulangi b-d sampai populasi baru penuh
     f. Urutkan populasi baru berdasarkan heuristik
4. Kembalikan individu terbaik
```

### Operator Genetika

**Seleksi (roulette wheel).** Bobot setiap individu adalah `heuristikTerburuk + 1 - heuristikIndividu`, sehingga individu dengan penalti kecil punya peluang terpilih lebih besar.

**Crossover.** Strategi dipilih acak untuk setiap pasangan induk:

| Peluang | Strategi | Deskripsi |
|---------|----------|-----------|
| 40% | Two-point | Papan diratakan, segmen di antara dua titik potong ditukar antar induk |
| 30% | Horizontal row | Baris sampai titik potong dari induk A, sisanya dari induk B (dan sebaliknya) |
| 30% | Checkerboard | Sel dengan `(i + j)` genap dari induk A, sisanya dari induk B (dan sebaliknya) |

**Mutasi.** Pada setiap baris, satu sel dipilih acak lalu warnanya dibalik (0 menjadi 1 atau sebaliknya).

**Elitisme.** `elitismPct * populationSize` individu terbaik disalin langsung ke generasi berikutnya agar solusi bagus tidak hilang.

### Parameter

Parameter dibaca dari `params.txt`, satu nilai per baris dengan urutan tetap:

| Baris | Parameter | Contoh | Keterangan |
|-------|-----------|--------|------------|
| 1 | `maxGenerations` | `500` | Batas maksimum generasi |
| 2 | `populationSize` | `300` | Jumlah individu per generasi |
| 3 | `elitismPct` | `0.15` | Persentase elit yang dipertahankan |
| 4 | `crossoverRate` | `0.8` | Peluang crossover |
| 5 | `mutationRate` | `0.05` | Peluang mutasi per anak |

## Struktur Proyek

```
YinYangSolver/
├── YinYangCell.java        # Individu: papan, fungsi heuristik, mutasi
├── YinyangPopulation.java  # Populasi: seleksi, crossover, elitisme, offspring
├── YinyangTester.java      # Entry point: baca input, jalankan GA, tulis output
├── params.txt              # Parameter Algoritma Genetika
├── test.txt                # Test case (input)
├── output.txt              # Hasil (dibuat otomatis saat program dijalankan)
└── README.md
```

## Persyaratan

- Java Development Kit (JDK) 8 atau lebih baru

## Cara Menjalankan

1. Clone repository ini.
   ```bash
   git clone <url-repository-anda>
   cd YinYangSolver
   ```
2. Isi test case di `test.txt` dan atur parameter di `params.txt` (lihat [Format File](#format-file)).
3. Kompilasi seluruh file Java.
   ```bash
   javac *.java
   ```
4. Jalankan program.
   ```bash
   java YinyangTester
   ```
   Tidak ada output di terminal, tetapi program tetap berjalan. Tunggu sampai proses selesai.
5. Buka `output.txt` untuk melihat hasilnya.

> Jalankan perintah dari folder yang berisi `test.txt` dan `params.txt`, karena file dibaca menggunakan path relatif.

## Format File

### `test.txt`

```
N
n
<papan n x n>

n
<papan n x n>
...
```

- `N` : jumlah test case
- `n` : ukuran papan (`n x n`)
- papan : `n` baris berisi `n` angka (`-1` kosong, `0` putih, `1` hitam), dipisahkan spasi

Contoh satu test case 6x6:

```
6
1 0 1 1 -1 0
-1 1 0 0 1 0
1 -1 1 1 0 0
0 1 0 1 1 -1
-1 1 0 -1 1 0
0 0 1 -1 0 1
```

### `params.txt`

```
500
300
0.15
0.8
0.05
```

### `output.txt`

Untuk setiap test case, program menuliskan papan terbaik beserta statusnya, lalu di akhir menuliskan akurasi:

```
Test Case #1
Fittest Solution:
1 0 1 1 0 0
...

Solution found in 42 generations

Accuracy = 0.35
Correct solutions: 35 out of 100
```

Jika tidak ada solusi sempurna, pesan yang muncul adalah `No perfect solution. Best heuristic: <nilai> in <generasi> generations`.

## Contoh

Berikut contoh solusi 6x6 yang valid menurut ketiga aturan puzzle (`1` = hitam, `0` = putih): kedua warna masing-masing terhubung dan tidak ada blok 2x2 satu warna.

```
1 1 0 0 0 0
0 1 1 1 1 0
0 0 0 0 1 0
1 1 1 0 1 0
1 0 1 0 1 0
1 0 1 1 1 0
```

## Keterbatasan

Beberapa hal yang perlu diketahui dari implementasi saat ini:

- **Clue pada papan belum dipakai.** Angka `0`/`1` pada papan input belum dipaksakan di fungsi heuristik maupun saat inisialisasi, sehingga solver belum menjamin hasil sesuai dengan sel yang sudah terisi pada puzzle.
- **Penalti jumlah hitam = putih bukan aturan resmi puzzle.** Penalti ini bisa menolak solusi yang sebenarnya valid dan membuat heuristik tidak pernah mencapai 0 untuk `n` ganjil.
- **`crossoverRate` belum digunakan.** Parameter dibaca dari `params.txt`, tetapi crossover selalu dilakukan.
- **Hasil tidak dijamin optimal.** Algoritma Genetika bersifat stokastik, jadi akurasi bergantung pada parameter dan seed. Seed saat ini tetap (`new Random(2)`) agar hasil dapat direproduksi.
- **Skala ukuran papan.** Semakin besar papan, semakin besar ruang pencarian, sehingga waktu dan peluang gagal ikut meningkat.

## Pengembangan Selanjutnya

- Menambahkan penalti jika sel clue diubah, atau mengunci sel clue saat inisialisasi, crossover, dan mutasi
- Menghapus penalti selisih jumlah hitam-putih agar sesuai aturan resmi
- Menggunakan `crossoverRate` pada proses reproduksi
- Menambahkan operator perbaikan (repair) atau local search untuk mempercepat konvergensi
- Menampilkan progres di terminal dan menyimpan waktu eksekusi

## Referensi

- [Yin-Yang Puzzle](https://www.puzzle-yin-yang.com/)