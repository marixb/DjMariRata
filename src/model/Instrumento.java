package model;

public class Instrumento implements Runnable {
    private String nome;
    private AudioPlayer audioPlayer;
    private volatile boolean rodando;
    private boolean pausado;
    private int bpm;
    private Thread thread;

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
            int tempoEspera;

            synchronized (this) {
                while (pausado && rodando) {
                    audioPlayer.pausar();
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        audioPlayer.parar();
                        return;
                    }
                }

                if (!rodando) {
                    break;
                }

                audioPlayer.tocar();
                tempoEspera = 60000 / bpm;
            }

            try {
                Thread.sleep(tempoEspera);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        audioPlayer.parar();
    }

    public synchronized void iniciar() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.setName(nome);
            thread.start();
            System.out.println("🎵 " + nome + " iniciado!");
        }
    }

    public synchronized void alternarPausa() {
        if (pausado) {
            pausado = false;
            this.notifyAll();
            System.out.println("▶ " + nome + " retomado!");
        } else {
            pausado = true;
            System.out.println("⏸ " + nome + " pausado!");
        }
    }

    public void parar() {
        Thread threadLocal;
        synchronized (this) {
            rodando = false;
            pausado = false;
            this.notifyAll();
            threadLocal = thread;
        }

        try {
            if (threadLocal != null && threadLocal.isAlive()) {
                threadLocal.interrupt();
                threadLocal.join(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("⏹ " + nome + " finalizado!");
    }

    public synchronized String getNome() {
        return nome;
    }

    public synchronized boolean isPausado() {
        return pausado;
    }

    public boolean isRodando() {
        return rodando;
    }

    public synchronized int getBpm() {
        return bpm;
    }

    public synchronized void setBpm(int bpm) {
        this.bpm = bpm;
        System.out.println("🔁 BPM do " + nome + " alterado para " + bpm);
    }

    public synchronized String getStatus() {
        if (!rodando) return "❌ PARADO";
        return pausado ? "⏸ PAUSADO" : "▶ TOCANDO";
    }
}