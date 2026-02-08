FROM quay.io/wildfly/wildfly:latest

# --- Machine User (For Worker <-> Master communication) ---
ENV WILDFLY_USER admin
ENV WILDFLY_PASS Admin#123
RUN /opt/jboss/wildfly/bin/add-user.sh -u $WILDFLY_USER -p $WILDFLY_PASS --silent

# --- Human User (For YOU to log in) ---
# We give THIS user the SuperUser flag
RUN /opt/jboss/wildfly/bin/add-user.sh -u manager -p Manager#123 -g SuperUser --silent

# --- Enable Monitoring/Statistics for all subsystems ---
COPY --chmod=644 enable-monitoring.cli /tmp/enable-monitoring.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/enable-monitoring.cli

# --- Clean up history from embedded CLI to avoid conflicts ---
RUN rm -rf /opt/jboss/wildfly/domain/configuration/domain_xml_history/* \
    && rm -rf /opt/jboss/wildfly/domain/configuration/host_xml_history/*

# --- Auto-deployment: Copy WAR files to be deployed on startup ---
# Place your WAR files in master/deployments/ folder
RUN mkdir -p /opt/jboss/wildfly/deployments
COPY --chmod=644 deployments/ /opt/jboss/wildfly/deployments/

# --- Startup script with auto-deployment ---
COPY --chmod=755 start-with-deploy.sh /opt/jboss/wildfly/start-with-deploy.sh