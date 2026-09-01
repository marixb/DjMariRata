package controller;

import model.Instrumento;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DJController {
    private Map<String, Instrumento> instrumentos;
    private Thread threadStatus;
    private boolean statusRodando;
    
    public DJController() {
        this.instrumentos = new ConcurrentHashMap<>();
        this.statusRodando = true;
        iniciarThreadStatus();
    }

    public synchronized void adicionarInstrumento(String nome, String arquivoSom, int bpm) {
        if (instrumentos.containsKey(nome)) {
            System.out.println("⚠️ Instrumento já existe: " + nome);
            return;
        }
        
        Instrumento instrumento = new Instrumento(nome, arquivoSom, bpm);
        instrumentos.put(nome, instrumento);
        instrumento.iniciar();
        System.out.println("✅ " + nome + " adicionado (BPM: " + bpm + ")");
    }

    public synchronized void removerInstrumento(String nome) {
        Instrumento instrumento = instrumentos.remove(nome);
        if (instrumento != null) {
            instrumento.parar();
            System.out.println("🗑 " + nome + " removido!");
        } else {
            System.out.println("❌ Instrumento não encontrado: " + nome);
        }
    }
    
    public synchronized void alternarPausa(String nome) {
        Instrumento instrumento = instrumentos.get(nome);
        if (instrumento != null) {
            instrumento.alternarPausa();
        } else {
            System.out.println("❌ Instrumento não encontrado: " + nome);
        }
    }

    public synchronized void alterarBpm(String nome, int bpm) {
        Instrumento instrumento = instrumentos.get(nome);
        if (instrumento != null) {
            instrumento.setBpm(bpm);
        } else {
            System.out.println("❌ Instrumento não encontrado: " + nome);
        }
    }

    public synchronized void mostrarStatus() {
        System.out.println("\n=== 🎧 MESA DO DJ ===");
        if (instrumentos.isEmpty()) {
            System.out.println("Nenhum instrumento ativo");
        } else {
            for (Instrumento inst : instrumentos.values()) {
                System.out.printf("%-15s | %-12s | BPM: %3d%n", 
                    inst.getNome(), 
                    inst.getStatus(), 
                    inst.getBpm());
            }
        }
        System.out.println("=====================\n");
    }
    
    private void iniciarThreadStatus() {
        threadStatus = new Thread(() -> {
            while (statusRodando) {
                try {
                    Thread.sleep(3000); // Atualiza a cada 3 segundos
                    mostrarStatus();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        threadStatus.setDaemon(true);
        threadStatus.start();
    }
    
    public synchronized void finalizar() {
        statusRodando = false;
        if (threadStatus != null) {
            threadStatus.interrupt();
        }
        
        for (Instrumento inst : instrumentos.values()) {
            inst.parar();
        }
        instrumentos.clear();
        System.out.println("👋 Sistema finalizado!");
    }
    
    // Getter sincronizado para lista de instrumentos
    public synchronized String[] getNomesInstrumentos() {
        return instrumentos.keySet().toArray(new String[0]);
    }
}