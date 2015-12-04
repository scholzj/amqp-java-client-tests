1) Modify settings.properties to point to your test broker
2) Run the default tests against MRG-M 3.2.0 with "mvn test" command
     or
   Run some other test suite with "mvn test -Dsurefire.suiteXmlFiles=<XMLTestSuite>" command. Available test suites are mrg-3.0.0.xml, mrg-3.2.0.xml and qpid-0.34.xml
