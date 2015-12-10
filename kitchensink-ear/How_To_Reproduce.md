How to reproduce CDI overlay issue
==================================

Issue: https://bugzilla.redhat.com/show_bug.cgi?id=1287732

Steps
-------------------------

1. In Linux Terminal, run:
> mvn clean install

2. In WildFly/EAP6 CLI, run:
> deploy ear/target/wildfly-kitchensink-ear.ear

3. Open web browser, visit: http://127.0.0.1:8080/wildfly-kitchensink-ear-web/index.jsf, register a member in web page.
There will be a WARN level log message in server log: 'Interceptored!!!'

4. In WildFly/EAP6 CLI, run:
> deployment-overlay add --name=EAR-OVERLAY --content=/wildfly-kitchensink-ear-ejb.jar/META-INF/jboss-ejb3.xml=/sources/wildfly-quickstart/kitchensink-ear/jboss-ejb3.xml --deployments=wildfly-kitchensink-ear.ear --redeploy-affected

`NOTE: change the path of jboss-ejb3.xml to be overlayed!!`

5. Repeat step 3, there will be a WARN level log message in server log: 'Interceptored in overlay !!!'


`If step 4 fails, then the issue is reproduced, if step 4 succeeds, going on step 5 to get expected log message.`

