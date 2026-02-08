#!/bin/bash

# Build and Deploy EJB/JMS Application to WildFly Domain
set -e

echo "========================================"
echo "Building EJB/JMS Application"
echo "========================================"

cd "$(dirname "$0")"

# Build the WAR file
mvn clean package -DskipTests

echo ""
echo "========================================"
echo "Configuring JMS Queue on WildFly"
echo "========================================"

# Configure JMS Queue (run against domain controller)
docker exec jboss-1-domain-primary-1 /opt/jboss/wildfly/bin/jboss-cli.sh --connect controller=localhost:9990 --commands="
/profile=full/subsystem=messaging-activemq/server=default/jms-queue=DemoQueue:add(entries=[\"java:/jms/queue/DemoQueue\", \"java:jboss/exported/jms/queue/DemoQueue\"])
" 2>/dev/null || echo "Queue may already exist, continuing..."

echo ""
echo "========================================"
echo "Deploying Application to Server Group"
echo "========================================"

# Copy WAR to domain controller
docker cp target/ejb-jms-app.war jboss-1-domain-primary-1:/tmp/

# Deploy to server group
docker exec jboss-1-domain-primary-1 /opt/jboss/wildfly/bin/jboss-cli.sh --connect controller=localhost:9990 --commands="
deploy /tmp/ejb-jms-app.war --server-groups=main-server-group --force
"

echo ""
echo "========================================"
echo "Deployment Complete!"
echo "========================================"
echo ""
echo "Access the application at:"
echo "  http://localhost/ejb-jms-app/"
echo ""
echo "Or directly on workers (if ports exposed):"
echo "  http://localhost:8080/ejb-jms-app/"
echo ""
