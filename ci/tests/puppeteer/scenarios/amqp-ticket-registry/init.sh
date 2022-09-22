#!/bin/bash
chmod +x "${PWD}/ci/tests/rabbitmq/run-rabbitmq-server.sh"
"${PWD}/ci/tests/rabbitmq/run-rabbitmq-server.sh"

echo -e "Removing previous SAML metadata directory, if any"
rm -Rf "${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/saml-md"
echo -e "Creating SAML metadata directory"
mkdir "${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/saml-md"
chmod +x "${PWD}/ci/tests/saml2/run-saml-server.sh"
"${PWD}/ci/tests/saml2/run-saml-server.sh"
