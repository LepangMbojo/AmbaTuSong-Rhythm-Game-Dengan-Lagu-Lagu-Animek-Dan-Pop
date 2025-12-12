# AmbaTuSong-Rhythm-game-dengan-lagu-lagu-daerah-dan-pop

## Anggota Kelompok:
1. M.Khalid Al Rejeki (F1D02310122)
2. Salsa Reike Maharani (F1D02310136)
3. Zainul Majdi (F1D02310028)
4. Nur Adinda Juniarti (F1D02310129)
## Deskripsi Program:
Rhythm Game "Ambatusong" adalah sebuah game ritme (rhythm game) 2D yang dikembangkan menggunakan bahasa pemrograman Java dengan pendekatan Object-Oriented Programming (OOP). Game ini terinspirasi dari game rhythm terkenal seperti osu!, Guitar Hero, dimana pemain harus menekan tombol keyboard sesuai dengan timing yang tepat mengikuti irama musik.

## Mekanisme Game
### 1. Tap Note (Note Tekan):

- Note biasa yang muncul di lane tertentu

- Pemain harus menekan tombol tepat ketika note mencapai garis hit

- Menghilang setelah berhasil ditekan atau terlewat

### 2. Hold Note (Note Tahan):

- Note dengan durasi panjang yang ditampilkan sebagai "ekor"

- Pemain harus menahan tombol dari awal sampai akhir note

- Sistem penilaian berdasarkan ketepatan melepas tombol

### 3. Sistem Penilaian (Judgement)
- PERFECT (±0.05 detik): 300 poin + combo multiplier

- GOOD (±0.12 detik): 100 poin + combo multiplier

- MISS (±0.5 detik): 0 poin + reset combo

### 4. Sistem Combo & Score
- Combo meningkat dengan setiap note yang berhasil (kecuali MISS)

- Multiplier score: 1 + (combo / 100)

- Score = Base Score × Multiplier
