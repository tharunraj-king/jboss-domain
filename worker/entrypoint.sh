#!/bin/bash
set -e

# THE FIX:
# Use double quotes ("...") so Bash expands $JBOSS_HOST_NAME to 'worker1'
# We use pipes (|) as delimiters to avoid escaping issues with slashes
echo "Patching host-secondary.xml with hostname: $JBOSS_HOST_NAME"
sed -i "s|\${jboss.host.name}|$JBOSS_HOST_NAME|g" /opt/jboss/wildfly/domain/configuration/host-secondary.xml

# Execute the command passed from docker-compose
exec "$@"