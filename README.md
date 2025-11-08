🚀 QueueCTL — CLI-based Background Job Queue System

Author: Jaswanth Kumar Yaramasa
Tech Stack: Java 17, Maven, Picocli, Gson
Mode: CLI Application
Assignment: Flam Backend Developer Internship

🎯 Objective

queuectl is a CLI-based background job queue system that allows you to enqueue background jobs, execute them through worker processes, automatically retry failed ones with exponential backoff, and move permanently failed jobs into a Dead Letter Queue (DLQ).

⚙️ Setup Instructions
1️⃣ Clone the Repository
git clone https://github.com/JaswanthKumarYaramasa/queuectl.git
cd queuectl

2️⃣ Build the Project
mvn clean package


This will generate a runnable JAR file in the target folder:
queuectl-1.0-SNAPSHOT-jar-with-dependencies.jar

3️⃣ Run Commands

Example (Windows):

java -jar target/queuectl-1.0-SNAPSHOT-jar-with-dependencies.jar --help

💻 Usage Examples
Category	Command	Description
Enqueue	java -jar queuectl.jar enqueue "echo Hello"	Adds a new job to the queue
Workers	java -jar queuectl.jar worker --start --count 3	Starts 3 worker threads
Stop Workers	java -jar queuectl.jar worker --stop	Gracefully stops all workers
Status	java -jar queuectl.jar status	Shows job counts & active workers
List Jobs	java -jar queuectl.jar list --state pending	Lists jobs by state
DLQ	java -jar queuectl.jar dlq list	Shows jobs in the dead letter queue
Config	java -jar queuectl.jar config set max_retries 5	Updates config dynamically
Config Get	java -jar queuectl.jar config get backoff_base	Shows config values
🧩 Architecture Overview
🔄 Job Lifecycle
State	Description
pending	Waiting to be picked by a worker
processing	Currently being executed
completed	Successfully executed
failed	Failed but retryable
dead	Permanently failed (moved to DLQ)
🧠 Core Components

Main.java — Entry point, delegates to CLIHandler

CLIHandler.java — Handles subcommands (enqueue, worker, status, etc.)

WorkerManager.java — Manages worker threads (start/stop)

Worker.java — Executes system commands and handles retries

JobQueue.java — Manages pending, running, failed, and DLQ jobs

FileStorage.java — Persists jobs across restarts (jobs.json)

ConfigManager.java — Dynamic configuration (retries, backoff, timeout)

⚙️ System Requirements Implemented

✅ Job Execution (cmd/bash execution)
✅ Retry with exponential backoff (delay = base ^ attempts)
✅ DLQ for permanently failed jobs
✅ Persistent storage (JSON file)
✅ Multi-worker support
✅ Graceful shutdown
✅ Configurable retry count, timeout, and backoff base

🧪 Testing Instructions

You can verify functionality step by step:

# Start workers
java -jar queuectl.jar worker --start --count 3

# Add jobs
java -jar queuectl.jar enqueue "echo Hello"
java -jar queuectl.jar enqueue "ping -n 5 127.0.0.1 >NUL"

# Show status
java -jar queuectl.jar status

# View DLQ
java -jar queuectl.jar dlq list


✅ Expected:

“echo Hello” completes successfully

“timeout/ping” jobs respect timeout & retries

DLQ shows permanently failed jobs

## 📹 Demo Video
🎥 [Watch the CLI demo here](https://drive.google.com/file/d/1EruPAb9EU0xFgHaPFMV3oDysPff1eQ95/view?usp=drive_link)
