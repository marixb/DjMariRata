

import controller.DJController;
import java.util.Scanner;

public class MainConsole {
    public static void main(String[] args) {
        DJController controller = new DJController();
        Scanner scanner = new Scanner(System.in);
        
        controller.adicionarInstrumento("Bateria", "sounds/bateria.wav", 120);
        controller.adicionarInstrumento("Baixo", "sounds/baixo.wav", 110);
        controller.adicionarInstrumento("Synth", "sounds/synth.wav", 130);
        
        System.out.println("\n=== 🎧 DJ MIXER CONSOLE ===");
        System.out.println("Comandos:");
        System.out.println("  play <nome>    - Pausar/Retomar instrumento");
        System.out.println("  add <nome> <bpm> - Adicionar instrumento");
        System.out.println("  remove <nome>  - Remover instrumento");
        System.out.println("  bpm <nome> <valor> - Alterar BPM");
        System.out.println("  status         - Mostrar status");
        System.out.println("  exit           - Sair");
        System.out.println("============================\n");
        
        while (true) {
            System.out.print("DJ > ");
            String linha = scanner.nextLine().trim();
            String[] partes = linha.split(" ");
            
            if (partes.length == 0) continue;
            
            switch (partes[0].toLowerCase()) {
                case "play":
                    if (partes.length > 1) {
                        controller.alternarPausa(partes[1]);
                    }
                    break;
                    
                case "add":
                    if (partes.length > 2) {
                        try {
                            int bpm = Integer.parseInt(partes[2]);
                            controller.adicionarInstrumento(partes[1], 
                                "sounds/" + partes[1] + ".wav", bpm);
                        } catch (NumberFormatException e) {
                            System.out.println("BPM inválido!");
                        }
                    }
                    break;
                    
                case "remove":
                    if (partes.length > 1) {
                        controller.removerInstrumento(partes[1]);
                    }
                    break;
                    
                case "bpm":
                    if (partes.length > 2) {
                        try {
                            int bpm = Integer.parseInt(partes[2]);
                            controller.alterarBpm(partes[1], bpm);
                        } catch (NumberFormatException e) {
                            System.out.println("BPM inválido!");
                        }
                    }
                    break;
                    
                case "status":
                    controller.mostrarStatus();
                    break;
                    
                case "exit":
                    controller.finalizar();
                    scanner.close();
                    System.out.println("👋 Até logo!");
                    System.exit(0);
                    break;
                    
                default:
                    System.out.println("❌ Comando desconhecido");
            }
        }
    }
}