#!/bin/bash
# Startup script for WildFly domain controller with auto-deployment

# Start WildFly in the background
/opt/jboss/wildfly/bin/domain.sh --host-config=host-primary.xml -b 0.0.0.0 -bmanagement 0.0.0.0 &
WILDFLY_PID=$!

# Wait for the server to start
echo "Waiting for WildFly to start..."
sleep 30

# Check if there are any WAR files to deploy
DEPLOY_DIR="/opt/jboss/wildfly/deployments"
if [ -d "$DEPLOY_DIR" ] && [ "$(ls -A $DEPLOY_DIR/*.war 2>/dev/null)" ]; then
    echo "Found WAR files to deploy..."
    
    for warfile in $DEPLOY_DIR/*.war; do
        if [ -f "$warfile" ]; then
            warname=$(basename "$warfile")
            echo "Deploying $warname..."
            /opt/jboss/wildfly/bin/jboss-cli.sh -c --user=admin --password='Admin#123' \
                --command="deploy $warfile --server-groups=main-server-group" 2>/dev/null
            
            if [ $? -eq 0 ]; then
                echo "Successfully deployed $warname"
            else
                echo "Failed to deploy $warname (may already be deployed)"
            fi
        fi
    done
    
    echo "Auto-deployment complete."
else
    echo "No WAR files found in $DEPLOY_DIR"
fi

# Wait for the WildFly process
wait $WILDFLY_PID
