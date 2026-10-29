# Restaurant Rush (SC2002) — Stage 1

Turn-based restaurant management game (command line, Java). You are the manager: each turn you choose one task
for the Waiter and one for the Chef; the Cashier works automatically.

Stage 1 = Regular customers, 3-item menu, Waiter / Chef / Cashier, full turn sequence, victory and defeat.
(No VIP, Critic, Host, Combo or Happy Hour — those belong to Stage 2 / 3.)

## Build and run

Requires JDK 11 or newer (tested on 21).

```bash
# macOS / Linux / Git Bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out Main
```

```bat
:: Windows (cmd)
dir /s /b src\*.java > sources.txt
mkdir out
javac -d out @sources.txt
java -cp out Main
```

## Reproducible runs (test notes)

The game reads one option number per prompt from standard input, so a script reproduces a run exactly.
If the script runs out of lines, the manager chooses *Wait* for every remaining task.

| Run     | Command                                | Result                                               |
|---------|----------------------------------------|------------------------------------------------------|
| Victory | `java -cp out Main < runs/victory.txt` | 5 paid, $66.00, average satisfaction 79.2 -> VICTORY |
| Defeat  | `java -cp out Main < runs/defeat.txt`  | Manager always waits: 0 paid, $0.00 -> DEFEAT        |

`runs/victory.txt` follows the priority serve > take order > seat > cook > wait.

## Project structure (BCE)

```
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

## Documented rules

- **Food choice (deterministic):** customer #n orders menu item `(n - 1) mod 3` -> Salad, Burger, Pasta, Salad, Burger, Pasta.
  The choice is shown as soon as the order is taken.
- **Start-of-turn display** appears after the turn's arrival, so the new customer is already in the queue.
- **Only valid tasks are offered.** The controller builds the list of legal Waiter / Chef options; out-of-range or
  non-numeric input is explained and asked again without using up the task.
- **Waiter acts before the Chef**, so the Chef can cook an order the Waiter just took, but a dish finished this turn
  can only be served next turn.
- **Cashier picks the customer served earliest** ("oldest first"). A customer who becomes READY_TO_PAY this turn
  pays next turn (payment is processed before eating inside `executeTurn`, which guarantees this).
- **Abandonment:** customers who are WAITING, SEATED or ORDERED lose 8 satisfaction at the end of the turn (min 0).
  At 0 they leave, their order is CANCELLED and the table is freed.

## Differences between the class diagram and the code

Update the Stage 1 class diagram with these so that diagram and code match.

| Diagram                                      | Code                                                                 | Why |
|----------------------------------------------|----------------------------------------------------------------------|-----|
| `UserInterface.displayState(state: GameOutcome)` | `displayState(turn, maxTurns, restaurant, result)`               | The start-of-turn display needs the full state, not just the outcome |
| `UserInterface.displayResult()`              | `displayResult(result, cfg, turnsUsed)`                              | Needs the data to show |
| *(not in diagram)*                           | `UserInterface.displayMessage(String)`                               | Shows events such as "Customer #1 orders Pasta" |
| *(not in diagram)*                           | `Decision` class with nested enum `Decision.Type`                    | `promptDecision` and `processWaiterAction` already use `Decision`; it needs a definition |
| `Customer.setServed()`                       | `setServed(turn: int)`                                               | Records `turnServed` |
| `Customer.getTurnServed(): void`             | `getTurnServed(): int`                                               | Typo in diagram |
| `Customer.chooseMenuItem()`                  | `chooseMenuItem(menu: List<MenuItem>)`                               | Customer needs the menu to choose from |
| *(not in diagram)*                           | `Customer.order`, `Customer.isWaitingForService()`                   | Realises the "places" association; used for satisfaction loss |
| *(not in diagram)*                           | `Order.customer`, `Order.isFullyPrepared()`, `Order.markCancelled()` | Cashier needs the customer's satisfaction; CANCELLED status needs a setter |
| `Order.markReady()` / `markServed()`         | `markReady(turn)` / `markServed(turn)`                               | Record `turnReady` and `turnServed` |
| `Order.advancePreparation()`                 | unchanged; the Chef marks the order READY                            | — |
| `MenuItem.basedPrice`                        | `basePrice`                                                          | Typo in diagram |
| `WaitingQueue.getsize()`                     | `getSize()`                                                          | Java naming |
| *(not in diagram)*                           | `WaitingQueue.remove(c)`, `getCustomers()`                           | The manager chooses *which* queued customer to seat |
| *(not in diagram)*                           | `Restaurant.getTables()`, `getMenu()`, `findTableOf(c)`              | Display, food choice, freeing a table |
| `Waiter.serve(o)` / `Chef.prepareOrder(o)`   | `serve(o, turn)` / `prepareOrder(o, turn)`                           | Turn is recorded on order and customer |
| `Waiter` (no attributes)                     | holds a `Restaurant`                                                 | `takeOrder` needs the menu |
| `GameResult`                                 | adds `recordServed()`, `recordAbandonment()` and getters             | Tracks served and abandoned counts |
| `GameConfig`                                 | adds `isArrivalTurn(turn)` and getters                               | Keeps the arrival schedule (turns 1,4,...,16) out of the controller |
| UI -> controller arrows                      | controllers call the UI (`TurnController -> UserInterface`)          | The controller asks the UI for decisions; the UI holds no rules |

Money is `double` as in the diagram. Stage 1 prices are whole dollars, so this is exact; switch to integer cents
when Stage 3 introduces fractional prices (Combo 90% rounding).
