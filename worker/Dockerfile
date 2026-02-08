FROM quay.io/wildfly/wildfly:latest

ENV WILDFLY_USER admin
ENV WILDFLY_PASS Admin#123

# 1. Create Machine User
RUN /opt/jboss/wildfly/bin/add-user.sh -u $WILDFLY_USER -p $WILDFLY_PASS --silent

# 2. Copy Config
COPY host-slave.xml /opt/jboss/wildfly/domain/configuration/host-secondary.xml

# 3. Copy and Setup Entrypoint
COPY entrypoint.sh /opt/jboss/wildfly/bin/entrypoint.sh
USER root
RUN chmod +x /opt/jboss/wildfly/bin/entrypoint.sh
RUN chown jboss:jboss /opt/jboss/wildfly/domain/configuration/host-secondary.xml
USER jboss

# 4. Set the Entrypoint
ENTRYPOINT ["/opt/jboss/wildfly/bin/entrypoint.sh"]

# 5. Default Command (can be overridden by Compose)
CMD ["/opt/jboss/wildfly/bin/domain.sh", "--host-config=host-secondary.xml"]