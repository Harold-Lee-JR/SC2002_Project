# SC2002_Project
SC2002 Project - "Restaurant Rush" 
A turn-based, command-line restaurant management game written in Java.

> **Project status:** In development \
> **Group:** SCEF Grp 3 \
> **Team members:** BAJPAI KASHVI, SINHA AYATI, THIO ZHONG PING, HAROLD LEE JING RUI \

## 1. Project overview

In **Restaurant Rush**, the player manages a small restaurant over **18 turns**. Customers arrive, wait for tables, order food, eat, and pay. The player chooses how to prioritize staff tasks while balancing revenue and customer satisfaction. A complete game ends in **Victory** or **Defeat** after turn 18.

The project is developed in **three independently runnable GitHub-tagged stages**. Each stage builds on the previous one, demonstrating how object-oriented design supports changing requirements.

| Stage | Git tag | Scope |
|---|---|---|
| 1 — Basic operation | `stage-1` | Regular customers; Waiter, Chef, Cashier; orders, tables, full 18-turn game and both outcomes |
| 2 — Customer behaviour | `stage-2` | Add VIP and Critic customers through polymorphism |
| 3 — New role and menu policy | `stage-3` | Add Host, ComboMeal, and Happy Hour |

**Scope:** The command-line interface is sufficient; the game runs offline. The interface collects decisions and displays results; gameplay rules belong in domain and control classes.

## 2. Build and run (Stage 1)

Requires JDK 11 or newer.

```bash
# macOS / Linux / Git Bash
cd "Stage 1"
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out Main
```

```bat
:: Windows (cmd)
cd "Stage 1"
dir /s /b src\*.java > sources.txt
mkdir out
javac -d out @sources.txt
java -cp out Main
```

## 3. Reproducible runs (test notes)

The game reads one option number per prompt from standard input, so a script reproduces a run exactly.
If the script runs out of lines, the manager chooses *Wait* for every remaining task. Run from inside `Stage 1/`:

| Run     | Command                                | Result                                               |
|---------|----------------------------------------|------------------------------------------------------|
| Victory | `java -cp out Main < runs/victory.txt` | 5 paid, $66.00, average satisfaction 79.2 -> VICTORY |
| Defeat  | `java -cp out Main < runs/defeat.txt`  | Manager always waits: 0 paid, $0.00 -> DEFEAT        |

## 4. Project structure (BCE)

```
Stage 1/
  SC2002_Project_Class Diagram-stage1.png
  runs/
    victory.txt  defeat.txt
  src/
    Main.java                    entry point
    boundary/UserInterface.java  CLI: display state, read the manager's choice (no game rules)
    control/
      GameController.java        18-turn loop, then evaluates the result
      TurnController.java        one turn: arrivals, choices, kitchen/serving, eating, payment, abandonment
      Waiter.java  Chef.java  Cashier.java
    entity/
      GameConfig  GameResult  GameOutcome (enum)
      Restaurant  Table  WaitingQueue
      Customer  CustomerStatus (enum)
      Order  OrderStatus (enum)  MenuItem
      Decision                   one option the manager can pick (built by the controller, shown by the UI)
```
