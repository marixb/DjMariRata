package view;

import controller.DJController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DJInterface extends JFrame {

    private static final Color COR_FUNDO = new Color(245, 245, 247);
    private static final Color COR_LINHA = Color.WHITE;
    private static final Color COR_BORDA = new Color(220, 220, 224);
    private static final Color COR_TOCANDO = new Color(46, 160, 67);
    private static final Color COR_PAUSADO = new Color(150, 150, 155);
    private static final Color COR_REMOVER = new Color(217, 48, 48);
    private static final Color COR_BPM = new Color(30, 110, 220);

    private final DJController controller;
    private final JPanel painelLista;
    private final JTextArea areaLog;
    private JTextField campoNome;
    private JTextField campoBpm;
    private final Map<String, LinhaInstrumento> linhas = new LinkedHashMap<>();

    public DJInterface() {
        controller = new DJController();

        setTitle("DJ Mixer");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(720, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        JPanel topo = criarPainelTopo();
        add(topo, BorderLayout.NORTH);

        painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setBackground(COR_FUNDO);
        painelLista.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JScrollPane scrollLista = new JScrollPane(painelLista);
        scrollLista.setBorder(BorderFactory.createEmptyBorder());
        scrollLista.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollLista, BorderLayout.CENTER);

        areaLog = new JTextArea(6, 40);
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(30, 30, 32));
        areaLog.setForeground(new Color(120, 220, 140));
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaLog.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log"));
        scrollLog.setPreferredSize(new Dimension(0, 150));
        add(scrollLog, BorderLayout.SOUTH);

        carregarInstrumentosIniciais();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                finalizarSistema();
            }
        });
    }

    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        painel.add(new JLabel("Instrumento:"));
        campoNome = new JTextField(12);
        painel.add(campoNome);

        painel.add(new JLabel("BPM:"));
        campoBpm = new JTextField("120", 4);
        painel.add(campoBpm);

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.addActionListener(e -> adicionarInstrumento());
        painel.add(btnAdicionar);

        JButton btnStatus = new JButton("Status");
        btnStatus.addActionListener(e -> controller.mostrarStatus());
        painel.add(btnStatus);

        return painel;
    }

    private void carregarInstrumentosIniciais() {
        String[][] instrumentosIniciais = {
            {"Bateria", "sounds/drums.wav", "130"},
            {"Baixo", "sounds/bass.wav", "130"},
            {"Synth", "sounds/synth.wav", "130"},
            {"Guitarra", "sounds/guitar.wav", "130"}
        };
        for (String[] inst : instrumentosIniciais) {
            controller.adicionarInstrumento(inst[0], inst[1], Integer.parseInt(inst[2]));
            criarLinhaInstrumento(inst[0], Integer.parseInt(inst[2]));
        }
    }

    private void adicionarInstrumento() {
        String nome = campoNome.getText().trim();
        String bpmText = campoBpm.getText().trim();

        if (nome.isEmpty() || bpmText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (linhas.containsKey(nome)) {
            JOptionPane.showMessageDialog(this, "Já existe um instrumento com esse nome!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int bpm = Integer.parseInt(bpmText);
            String arquivoSom = "sounds/" + nome.toLowerCase() + ".wav";

            controller.adicionarInstrumento(nome, arquivoSom, bpm);
            criarLinhaInstrumento(nome, bpm);

            campoNome.setText("");
            campoBpm.setText("120");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "BPM deve ser um número!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarLinhaInstrumento(String nome, int bpm) {
        LinhaInstrumento linha = new LinhaInstrumento(nome, bpm);
        linhas.put(nome, linha);
        painelLista.add(linha.painel);
        painelLista.add(Box.createVerticalStrut(6));
        painelLista.revalidate();
        painelLista.repaint();
        adicionarLog(nome + " adicionado à mesa");
    }

    private void adicionarLog(String mensagem) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        areaLog.append("[" + timestamp + "] " + mensagem + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void finalizarSistema() {
        Object[] opcoes = {"Sim", "Não"};
        int confirm = JOptionPane.showOptionDialog(this,
            "Deseja realmente finalizar o DJ Mixer?",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoes,
            opcoes[1]);

        if (confirm == 0) {
            controller.finalizar();
            dispose();
            System.exit(0);
        }
    }

    private class LinhaInstrumento {
        final JPanel painel;
        final JLabel labelNome;
        final JLabel labelEstado;
        final JLabel labelBpm;
        boolean tocando = true;
        int bpmAtual;

        LinhaInstrumento(String nome, int bpm) {
            bpmAtual = bpm;

            painel = new JPanel(new BorderLayout(10, 0));
            painel.setBackground(COR_LINHA);
            painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
            painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBackground(COR_LINHA);

            labelNome = new JLabel(nome);
            labelNome.setFont(new Font("SansSerif", Font.BOLD, 15));

            labelEstado = new JLabel("Tocando");
            labelEstado.setFont(new Font("SansSerif", Font.PLAIN, 12));
            labelEstado.setForeground(COR_TOCANDO);

            labelBpm = new JLabel(bpmAtual + " BPM");
            labelBpm.setFont(new Font("SansSerif", Font.PLAIN, 12));
            labelBpm.setForeground(new Color(100, 100, 105));

            JPanel linhaTexto = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            linhaTexto.setBackground(COR_LINHA);
            linhaTexto.add(labelEstado);
            linhaTexto.add(labelBpm);

            info.add(labelNome);
            info.add(linhaTexto);

            JButton btnToggle = new JButton("Pausar");
            btnToggle.setFocusPainted(false);
            btnToggle.setOpaque(true);
            btnToggle.setContentAreaFilled(true);
            btnToggle.setBorderPainted(false);
            btnToggle.setBackground(COR_PAUSADO);
            btnToggle.setForeground(Color.WHITE);
            btnToggle.setPreferredSize(new Dimension(90, 34));

            JButton btnBpm = new JButton("BPM");
            btnBpm.setFocusPainted(false);
            btnBpm.setOpaque(true);
            btnBpm.setContentAreaFilled(true);
            btnBpm.setBorderPainted(false);
            btnBpm.setBackground(COR_BPM);
            btnBpm.setForeground(Color.WHITE);
            btnBpm.setPreferredSize(new Dimension(70, 34));

            JButton btnRemover = new JButton("Remover");
            btnRemover.setFocusPainted(false);
            btnRemover.setOpaque(true);
            btnRemover.setContentAreaFilled(true);
            btnRemover.setBorderPainted(false);
            btnRemover.setBackground(COR_REMOVER);
            btnRemover.setForeground(Color.WHITE);
            btnRemover.setPreferredSize(new Dimension(90, 34));

            JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            acoes.setBackground(COR_LINHA);
            acoes.add(btnToggle);
            acoes.add(btnBpm);
            acoes.add(btnRemover);

            painel.add(info, BorderLayout.CENTER);
            painel.add(acoes, BorderLayout.EAST);

            btnToggle.addActionListener(e -> {
                controller.alternarPausa(nome);
                tocando = !tocando;
                if (tocando) {
                    btnToggle.setText("Pausar");
                    btnToggle.setBackground(COR_PAUSADO);
                    labelEstado.setText("Tocando");
                    labelEstado.setForeground(COR_TOCANDO);
                } else {
                    btnToggle.setText("Tocar");
                    btnToggle.setBackground(COR_TOCANDO);
                    labelEstado.setText("Pausado");
                    labelEstado.setForeground(COR_PAUSADO);
                }
                adicionarLog(nome + (tocando ? " começou a tocar" : " foi pausado"));
            });

            btnBpm.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(DJInterface.this,
                    "Novo BPM para " + nome + ":", "Alterar BPM", JOptionPane.QUESTION_MESSAGE);

                if (input != null) {
                    try {
                        int novoBpm = Integer.parseInt(input.trim());
                        controller.alterarBpm(nome, novoBpm);
                        bpmAtual = novoBpm;
                        labelBpm.setText(bpmAtual + " BPM");
                        adicionarLog(nome + " BPM alterado para " + novoBpm);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(DJInterface.this, "BPM inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnRemover.addActionListener(e -> {
                controller.removerInstrumento(nome);
                Container pai = painel.getParent();
                int idx = -1;
                for (int i = 0; i < pai.getComponentCount(); i++) {
                    if (pai.getComponent(i) == painel) { idx = i; break; }
                }
                pai.remove(painel);
                if (idx >= 0 && idx < pai.getComponentCount()) {
                    pai.remove(idx);
                }
                pai.revalidate();
                pai.repaint();
                linhas.remove(nome);
                adicionarLog(nome + " removido");
            });
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new DJInterface().setVisible(true);
        });
    }
}