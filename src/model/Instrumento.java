package model;

public class Instrumento implements Runnable {
    private final String nome;
    private final AudioPlayer audioPlayer;
    private volatile boolean rodando;
    private volatile boolean pausado;
    private volatile int bpm;
    private Thread thread;
    private final Object lock = new Object();

    public Instrumento(String nome, String arquivoSom, int bpm) {
        this.nome = nome;
        this.audioPlayer = new AudioPlayer(arquivoSom);
        this.rodando = true;
        this.pausado = false;
        this.bpm = bpm;
    }

    @Override
    public void run() {
        while (rodando) {
            if (!pausado) {
                audioPlayer.tocar();
                int tempoEspera = 60000 / bpm;
                try {
                    Thread.sleep(tempoEspera);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } else {
                audioPlayer.pausar();
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        audioPlayer.parar();
    }

    public void iniciar() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.setName(nome);
            thread.setDaemon(true);
            thread.start();
            System.out.println("🎵 " + nome + " iniciado!");
        }
    }

    public void alternarPausa() {
        pausado = !pausado;
        if (!pausado) {
            synchronized (lock) {
                lock.notify();
            }
            System.out.println("▶ " + nome + " retomado!");
        } else {
            System.out.println("⏸ " + nome + " pausado!");
        }
    }

    public void parar() {
        rodando = false;
        pausado = false; 
        synchronized (lock) {
            lock.notify();
        }
        if (thread != null) {
            thread.interrupt();
        }
        System.out.println("⏹ " + nome + " finalizado!");
    }

    public String getNome() { return nome; }
    public boolean isPausado() { return pausado; }
    public boolean isRodando() { return rodando; }
    public int getBpm() { return bpm; }
    public void setBpm(int bpm) {
        this.bpm = bpm;
        System.out.println("BPM do " + nome + " alterado para " + bpm);
    }
    public String getStatus() {
        if (!rodando) return " PARADO";
        return pausado ? "⏸ PAUSADO" : "▶ TOCANDO";
    }
}