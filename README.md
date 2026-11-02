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

## Stage 1 -> Stage 2: customer behaviour extension

**New files**
- `Stage2/SRC/entity/VIPCustomer.java`: a type of Customer. Loses 5 satisfaction for each turn they wait and gets 10% off their bill.
- `Stage2/SRC/entity/CriticCustomer.java`: a type of Customer. Loses 12 satisfaction for each turn they wait and pays full price. We chose to make the Critic always order the most expensive item on the menu (if two items cost the same, the first one listed). With our menu that is Pasta, which takes 2 preparation units, so the Critic is harder to serve quickly.
- `Stage2/SRC/control/CustomerFactory.java`: decides what type each arriving customer is. Customers still arrive on turns 1, 4, 7, 10, 13 and 16, in this order: Regular, VIP, Regular, Critic, Regular, Regular. The order is fixed so every run can be repeated.
- `Stage2/victory.txt`, `Stage2/defeat.txt`: the inputs we used for our test runs, with the outputs saved next to them.

**Money now stored in cents**
The original Stage 1 implementation used `double` because its prices were whole-dollar amounts. In Stage 2 the VIP discount creates prices like $13.50, and the brief says that once fractional prices come up, money should be kept as integer cents and shown in dollars with two decimals. So before adding the new customers, we copied Stage 1 into Stage 2 and changed the money to `int` cents (Salad 900, Burger 1500, Pasta 1800):
- `MenuItem.java`, `Order.java`: prices are `int` cents.
- `GameConfig.java`: the revenue target is 4500 cents ($45).
- `GameResult.java`: revenue is added up in cents.
- `Restaurant.java`: menu prices written in cents.
- `UserInterface.java`, `TurnController.java`: amounts are turned back into dollars only when printed, e.g. 1350 becomes "$13.50".
The game plays exactly the same as Stage 1 after this change; the Stage 1 victory script still ends in VICTORY.

**Existing files we changed**
- `Customer.java`: added `calculateBill(MenuItem)`, which returns the full price for a regular customer, and `getTypeLabel()`, which gives the name shown in messages. The subclasses override these. `toString()` now uses `getTypeLabel()`, but regular customers still show up as "Customer #1" like before.
- `Order.java` (again): the order's price now comes from `customer.calculateBill(item)` instead of `item.getPrice()`. This way each customer type works out its own bill, and the price is still fixed once the order is taken.
- `TurnController.java`: new customers are now created by `CustomerFactory` instead of `new Customer(...)`. This is the only change. The rest of the turn works the same and never checks what type of customer it is dealing with.
- `Main.java`: updated the comment to say Stage 2.

**What still works**
- Regular customers behave the same as in Stage 1 (lose 8 per turn, pay full price, same food choice).
- The win and lose conditions, the six arrivals and the order of steps in each turn are unchanged.
- Our victory run ends in VICTORY with 4 customers paid, $58.50 revenue and an average satisfaction of 69.5. Our defeat run ends in DEFEAT. Both runs include the VIP (#2) and the Critic (#4).

**Effect on the design**
In Stage 1 we had already put the behaviour that differs between customers, like satisfaction loss and food choice, into methods in Customer. For Stage 2 we only had to add VIPCustomer and CriticCustomer and override those methods, so the controllers never need to check the customer type. This means a new customer type can be added as a new class without editing the turn logic (open/closed principle). CustomerFactory is the only class that refers to VIPCustomer and CriticCustomer by name, when customers are created.
