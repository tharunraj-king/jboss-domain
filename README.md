# WildFly Domain Cluster with Docker

A production-ready WildFly/JBoss EAP domain mode cluster running in Docker containers with load balancing, EJB, and JMS messaging.

<!-- Static Badges -->
![WildFly](https://img.shields.io/badge/WildFly-39.0.0-yellow?logo=redhat)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-orange?logo=eclipse)
![License](https://img.shields.io/badge/License-MIT-green)
![Java](https://img.shields.io/badge/Java-17+-red?logo=openjdk)
![Nginx](https://img.shields.io/badge/Nginx-Load%20Balancer-009639?logo=nginx)

## 🏗️ Architecture

```
                    ┌─────────────┐
                    │   NGINX     │
                    │   (LB:80)   │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│   Worker 1    │  │   Worker 2    │  │   Worker 3    │
│  (server-one) │  │  (server-one) │  │  (server-one) │
└───────┬───────┘  └───────┬───────┘  └───────┬───────┘
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │
                    ┌──────┴──────┐
                    │   Domain    │
                    │  Controller │
                    │  (Primary)  │
                    │   :9990     │
                    └─────────────┘
```

## ✨ Features

- **Domain Mode Clustering** - Centralized management of multiple WildFly instances
- **Load Balancing** - NGINX round-robin load balancer
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
git clone https://github.com/YOUR_USERNAME/wildfly-docker-cluster.git
cd wildfly-docker-cluster

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

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [WildFly](https://www.wildfly.org/) - The flexible, lightweight, managed application runtime
- [Red Hat JBoss EAP](https://www.redhat.com/en/technologies/jboss-middleware/application-platform) - Enterprise version

---

⭐ If you found this helpful, please star the repository!
