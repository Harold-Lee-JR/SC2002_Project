# SC2002_Project
SC2002 Project - "Restaurant Rush" 
A turn-based, command-line restaurant management game written in Java.

> **Project status:** In development
> **Group:** SCEF Grp 3 
> **Team members:** BAJPAI KASHVI, SINHA AYATI, THIO ZHONG PING, HAROLD LEE JING RUI

## 1. Project overview

In **Restaurant Rush**, the player manages a small restaurant over **18 turns**. Customers arrive, wait for tables, order food, eat, and pay. The player chooses how to prioritize staff tasks while balancing revenue and customer satisfaction. A complete game ends in **Victory** or **Defeat** after turn 18.

The project is developed in **three independently runnable GitHub-tagged stages**. Each stage builds on the previous one, demonstrating how object-oriented design supports changing requirements.

| Stage | Git tag | Scope |
|---|---|---|
| 1 — Basic operation | `stage-1` | Regular customers; Waiter, Chef, Cashier; orders, tables, full 18-turn game and both outcomes |
| 2 — Customer behaviour | `stage-2` | Add VIP and Critic customers through polymorphism |
| 3 — New role and menu policy | `stage-3` | Add Host, ComboMeal, and Happy Hour |

**Scope:** The command-line interface is sufficient; the game runs offline. The interface collects decisions and displays results; gameplay rules belong in domain and control classes.
