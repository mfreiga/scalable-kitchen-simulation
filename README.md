# Scalable Kitchen Simulation — Java Concurrency & Scheduling

This project simulates a **restaurant kitchen** where multiple cooks and waiters process orders concurrently.  
It demonstrates **scalability and synchronization** in Java using **threads, blocking queues, and executors**.

---

The goal is to model how real-world kitchens coordinate:
- **Orders** arrive dynamically.
- **Waiters** pick up orders and deliver them.
- **Cooks** prepare dishes in parallel.
- The system scales by adjusting the number of threads (cooks/waiters).

This is both a concurrency exercise and a foundation for studying **throughput, bottlenecks, and scheduling**.

---


- **Order**: encapsulates customer requests (id, dish, status).
- **Kitchen**: central class with thread-safe queues for pending and finished orders.
- **Cook**: worker thread that takes orders from the pending queue, simulates preparation time, and pushes results.
- **Waiter**: worker thread that picks up finished orders and "delivers" them.
- **Simulation**: entry point that starts the kitchen, spawns cooks and waiters, submits orders, and observes system throughput.

**Key Concepts:**
- `BlockingQueue<Order>` → thread-safe communication channel.
- `ExecutorService` → scalable thread pool for cooks and waiters.
- Synchronization ensures no race conditions in order status updates.

---

## ⚙️ Features

- Adjustable **number of cooks and waiters** to test scalability.
- Randomized **preparation times** to simulate real kitchen workloads.
- Automatic logging of order lifecycle (created → in preparation → finished → delivered).
- Demonstrates:
  - **Producer–consumer pattern**
  - **Thread synchronization**
  - **Load balancing with executors**

---

### Prerequisites
- Java 17 or newer
- Gradle or any IDE (IntelliJ / Eclipse / VS Code)

### Build
```bash
./gradlew clean build