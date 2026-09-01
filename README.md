# DJ Mixer — Mesa de DJ com Threads

Aplicação Java que simula uma mesa de DJ, onde cada instrumento (Bateria, Baixo, Synth, Guitarra) toca em sua própria *thread*, de forma independente. O DJ controla cada faixa individualmente pela interface gráfica — pausando, retomando, ajustando o BPM ou removendo instrumentos — sem afetar as demais faixas.

## Conceitos aplicados

- **Threads independentes**: cada instrumento (`Instrumento implements Runnable`) roda em sua própria `Thread`, tocando em loop contínuo até ser pausado ou encerrado.
- **Controle seguro de início/pausa/encerramento**: as threads nunca são finalizadas de forma abrupta (`Thread.stop()`). O encerramento usa uma flag de controle + `interrupt()` + `join()`, e a pausa usa `wait()`/`notifyAll()`.
- **Sincronização de estado**: os métodos que alteram o estado de um instrumento (`alternarPausa`, `setBpm`, `parar`, etc.) são `synchronized`, garantindo que apenas uma thread por vez modifique o estado de cada instrumento — evitando condições de corrida.
- **BPM controlando o ritmo**: o valor de `Thread.sleep()` dentro do loop de cada instrumento é calculado a partir do BPM (`60000 / bpm`), então BPM maior = intervalo menor = "batidas" mais rápidas.
- **Thread de status em tempo real**: uma thread separada (`threadStatus`, no `DJController`) imprime o status de todas as faixas a cada poucos segundos, funcionando como um painel ao vivo.
- **Adição dinâmica de instrumentos**: é possível adicionar um novo instrumento pela interface enquanto o sistema está rodando, sem parar as faixas já em execução.

## Estrutura do projeto

DjMariRata/
├── src/
│ ├── model/
│ │ ├── Instrumento.java # Thread de cada instrumento (lógica de tocar/pausar/parar)
│ │ └── AudioPlayer.java # Reprodução do áudio (.wav) via javax.sound.sampled
│ ├── controller/
│ │ └── DJController.java # Gerencia os instrumentos e a thread de status
│ ├── view/
│ │ └── DJInterface.java # Interface gráfica (Swing)
│ └── main/
│ └── MainConsole.java # Ponto de entrada via console
├── sounds/
│ ├── bateria.wav
│ ├── baixo.wav
│ ├── synth.wav
│ └── guitarra.wav
└── README.md

## Requisitos

- JDK 11 ou superior instalado (`java -version` / `javac -version` para conferir).
- Arquivos `.wav` na pasta `sounds/` com os nomes: `bateria.wav`, `baixo.wav`, `synth.wav`, `guitarra.wav`. Se algum arquivo não existir, a aplicação continua funcionando normalmente (só aquele instrumento fica sem áudio, com aviso no console).

## Como compilar e rodar

No PowerShell, a partir da raiz do projeto:

\`\`\`powershell
javac -d out src\\model\\*.java src\\controller\\*.java src\\view\\*.java src\\main\\*.java
java -cp out view.DJInterface
\`\`\`

Isso compila todas as classes para a pasta `out/` e inicia a interface gráfica.

## Como usar

1. Ao abrir, os instrumentos padrão (Bateria, Baixo, Synth, Guitarra) já começam tocando, todos a 130 BPM.
2. **Pausar/Retomar**: clique no botão de play/pause de cada instrumento para controlar sua faixa individualmente.
3. **Alterar BPM**: clique em "BPM" para digitar um novo valor — a velocidade do loop daquele instrumento muda imediatamente.
4. **Adicionar instrumento**: preencha o nome e o BPM no topo da tela e clique em "Adicionar" — o novo instrumento entra tocando junto com os demais.
5. **Remover instrumento**: clique em "Remover" para encerrar a thread daquele instrumento com segurança.
6. **Status**: o botão "Status" (e a thread de status em segundo plano) mostram o estado atual de todas as faixas no console.
7. Ao fechar a janela, a aplicação pede confirmação e finaliza todas as threads de forma controlada antes de encerrar.

## Equipe

- [João Pedro Cavalcanti Souza]
- [Lucas Henrique Gomes Medeiros]
- [Luis Felipe Farias Nunes]
- [Luis(fim) Lucena Wanderley]
- [Mariana Xavier Bezerra]
- [Micaella Maria Barbosa Cabral]

