package model;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioPlayer {
    private final String arquivoSom;
    private Clip clip;
    private boolean tocando;

    public AudioPlayer(String arquivoSom) {
        this.arquivoSom = arquivoSom;
        this.tocando = false;
        carregarClip();
    }

    private void carregarClip() {
        try {
            File arquivo = new File(arquivoSom);
            if (!arquivo.exists()) {
                System.out.println("⚠ Arquivo não encontrado: " + arquivoSom + " (rodando sem som para este instrumento)");
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivo);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (Exception e) {
            System.out.println("⚠ Erro ao carregar " + arquivoSom + ": " + e.getMessage());
        }
    }

    public void tocar() {
        tocando = true;
        if (clip == null) {
            return;
        }
        if (!clip.isRunning()) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void pausar() {
        tocando = false;
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void parar() {
        tocando = false;
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    public boolean isTocando() {
        return tocando;
    }
}