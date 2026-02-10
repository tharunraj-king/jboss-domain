# WildFly Domain Cluster with Docker

A production-ready WildFly/JBoss EAP domain mode cluster running in Docker containers with load balancing, EJB, and JMS messaging.

<!-- Static Badges -->
![WildFly](https://img.shields.io/badge/WildFly-39.0.0-yellow?logo=redhat)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-orange?logo=eclipse)
![License](https://img.shields.io/badge/License-MIT-green)
![Java](https://img.shields.io/badge/Java-17+-red?logo=openjdk)
![Nginx](https://img.shields.io/badge/Nginx-Load%20Balancer-009639?logo=nginx)

## 💡 Project Goal & Context

**TL;DR:** This is an **Infrastructure & Orchestration** project — I focused on Docker patterns, not JBoss internals.

This project serves as a **Reference Architecture** for containerizing legacy middleware. The goal was to solve the challenge of running **JBoss/WildFly Domain Mode**—originally designed for static VMs—inside a dynamic **Docker environment**.

**Key Challenges Solved:**
- **Dynamic Host Registration:** Workers waiting for Domain Controller via health checks
- **Network Topology:** Docker DNS for JBoss management interfaces, app ports behind load balancer
- **Session Affinity:** Sticky sessions at Nginx layer for stateful Jakarta EE apps

## 🏗️ Architecture

```
                         External
                     ┌─────────────┐
           :80 ────▶ │   NGINX     │
                     │    (LB)     │
                     └──────┬──────┘
                            │
       ┌────────────────────┼────────────────────┐
       │                    │                    │
       ▼                    ▼                    ▼
 ┌───────────┐       ┌───────────┐       ┌───────────┐
 │  Worker 1 │       │  Worker 2 │       │  Worker 3 │
 │  (350MB)  │       │  (350MB)  │       │  (350MB)  │
 └─────┬─────┘       └─────┬─────┘       └─────┬─────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
                    ┌──────┴──────┐
          :9990 ──▶ │   Domain    │
                    │  Controller │
                    │   (256MB)   │
                    └─────────────┘
                    
             [ wf-cluster bridge network ]
```

**Resource Requirements:** ~1.3GB RAM total (256MB primary + 350MB × 3 workers)

## ✨ Features

- **Domain Mode Clustering** - Centralized management of multiple WildFly instances
- **Load Balancing** - NGINX with sticky sessions (ip_hash)
- **Auto-Deployment** - Drop WAR files in `master/deployments/` for automatic deployment
- **Full Monitoring** - EJB, JMS, Undertow, Transactions, DataSources statistics enabled
- **JMS Messaging** - ActiveMQ Artemis with pre-configured queues
- **Health Checks** - Docker health checks ensure proper startup order

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 17+ and Maven (for building the demo app)

### Start the Cluster

```bash
# Clone the repository
git clone https://github.com/tharunraj-king/jboss-domain.git
cd jboss-domain

# Build and start
docker-compose up -d --build

# Check status
docker-compose ps
```

### Access Points

| Service | URL |
|---------|-----|
| **Application** | http://localhost/ |
| **Admin Console** | http://localhost:9990 |
| **EJB Demo** | http://localhost/ejb-jms-app/ejb |
| **JMS Demo** | http://localhost/ejb-jms-app/jms |

### Admin Console Credentials

- **Username:** `manager`
- **Password:** `Manager#123`

## 📁 Project Structure

```
.
├── docker-compose.yaml      # Container orchestration
├── master/                  # Domain Controller
│   ├── Dockerfile
│   ├── enable-monitoring.cli # Monitoring & JMS setup
│   ├── start-with-deploy.sh # Auto-deployment script
│   └── deployments/         # ⬅️ Drop WAR files here
├── worker/                  # Worker Nodes
│   ├── Dockerfile
│   └── host-slave.xml       # Secondary host config
├── nginx/                   # Load Balancer
│   └── nginx.conf
└── ejb-jms-app/            # Demo Application
    ├── pom.xml
    └── src/
```

## 🛠️ Deploy Your Application

1. Build your WAR file:
   ```bash
   cd ejb-jms-app
   mvn clean package
   ```

2. Copy to deployments folder:
   ```bash
   cp target/your-app.war ../master/deployments/
   ```

3. Restart the cluster:
   ```bash
   docker-compose up -d --build --force-recreate
   ```

## 📊 Monitoring

All monitoring is **enabled by default**:

| Subsystem | Statistics |
|-----------|-----------|
| **Undertow** | Request count, bytes, processing time |
| **EJB3** | Invocations, execution time, pool stats |
| **JMS/Messaging** | Message counts, queue depth, consumers |
| **Transactions** | Commits, rollbacks |
| **DataSources** | Connection pool stats |
| **Infinispan** | Cache hits/misses |

View via Admin Console: **Runtime → Monitoring**

## 🔧 Configuration

### Scaling Workers

Add more workers in `docker-compose.yaml`:

```yaml
worker4:
  build: ./worker
  image: wildfly-domain-worker-custom
  depends_on:
    domain-primary:
      condition: service_healthy
  environment:
    - JBOSS_HOST_NAME=worker4
    - JAVA_OPTS=-Xms64m -Xmx350m -Djava.net.preferIPv4Stack=true
  command: >
    /opt/jboss/wildfly/bin/domain.sh 
    --host-config=host-secondary.xml 
    -Djboss.domain.primary.address=domain-primary 
    -Djboss.bind.address=0.0.0.0 
    -Djboss.bind.address.management=0.0.0.0
  networks:
    - wf-cluster
```

Then update `nginx/nginx.conf` to include the new worker.

### Adding JMS Queues

Edit `master/enable-monitoring.cli`:

```
/profile=full/subsystem=messaging-activemq/server=default/jms-queue=MyNewQueue:add(entries=["java:/jms/queue/MyNewQueue", "java:jboss/exported/jms/queue/MyNewQueue"])
```

## 🐛 Troubleshooting

### Workers can't connect to domain controller

```bash
# Check domain-primary health
docker-compose ps
docker logs jboss-1-domain-primary-1

# Ensure health check passes before workers start
docker inspect jboss-1-domain-primary-1 | grep -A 10 Health
```

### Application not deploying

```bash
# Check deployment status
docker exec jboss-1-domain-primary-1 /opt/jboss/wildfly/bin/jboss-cli.sh \
  --connect --controller=localhost:9990 \
  --user=manager --password='Manager#123' \
  --command="deployment-info --server-group=main-server-group"
```

### View server logs

```bash
docker logs -f jboss-1-worker1-1
```

## 📝 Demo Application

The included `ejb-jms-app` demonstrates:

- **Stateless Session Bean** - `CalculatorBean` with arithmetic operations
- **JMS Producer** - Sends messages to a queue
- **Message-Driven Bean** - Asynchronously processes queue messages
- **Servlets** - Web interface to test EJB and JMS

## 📚 Sample App Topics

- **Overview:** The `ejb-jms-app` is a minimal Jakarta EE 10 sample demonstrating EJBs, JMS messaging (Artemis), and simple servlet-based UIs to exercise the app.
- **Endpoints:**
  - `GET /ejb-jms-app/ejb` — exercise the `CalculatorBean` via servlet
  - `GET /ejb-jms-app/jms` — send a JMS message via the JMS producer servlet
  - `GET /ejb-jms-app/` — app home page (simple UI)
- **Components:** Stateless session beans, a JMS producer, a Message-Driven Bean (MDB), and a few test servlets under `src/main/java` and `src/main/webapp`.
- **Build & Run:** Build with `mvn clean package` and copy the produced WAR to `master/deployments/` (or use the included pre-built WAR in `master/deployments/`).
- **Test Examples:**
  - Exercise EJB: `curl http://localhost/ejb-jms-app/ejb`
  - Exercise JMS: `curl http://localhost/ejb-jms-app/jms`
  - View server logs: `docker logs -f jboss-1-worker1-1`
- **Extending the Sample:** Add new EJBs or JMS queues, then update `master/enable-monitoring.cli` for queue definitions and drop the WAR into `master/deployments/`.
- **Troubleshooting:** If servlet calls fail, check the domain controller deployments via `jboss-cli.sh` and verify JMS queues exist using the Admin Console or CLI.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [WildFly](https://www.wildfly.org/) - The flexible, lightweight, managed application runtime
- [Red Hat JBoss EAP](https://www.redhat.com/en/technologies/jboss-middleware/application-platform) - Enterprise version

---

⭐ If you found this helpful, please star the repository!
