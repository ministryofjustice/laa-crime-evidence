# laa-crime-evidence

This is a Java based Spring Boot Application which will be hosted on AWS Environment (Cloud Platform). The application is being deployed on to the AWS ECS container service. This is a Facade application to the existing LAA legacy Applications MAAT/MLRA.

[High level design](https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/3673751570/Means+Assessment+-+High+level+Design+Approach)

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/ministryofjustice/laa-crime-evidence/tree/main.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/gh/ministryofjustice/laa-crime-evidence/tree/main)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Developer setup

1. Go through with this [Java Developer On-boarding Check List](https://dsdmoj.atlassian.net/wiki/spaces/ASLST/pages/3738468667/Java+Developer+Onboarding+Check+List/) and complete all tasks.
2. Request a team member to be added to the repository.
3. Create a GPG (more detail further down on the page) key and create a PR. Someone from the team will approve the PR.
4. This is a document to outline the general guideline [Developer Guidelines](https://dsdmoj.atlassian.net/wiki/spaces/ASLST/pages/3896049821/Developer+Guidelines).
5. This project has its own dedicated Jira Scrum board, and you can access [from here](https://dsdmoj.atlassian.net/jira/software/projects/LCAM/boards/881) and [project backlog](https://dsdmoj.atlassian.net/jira/software/projects/LCAM/boards/881/backlog)

### Pre-requisites

1. Docker
2. SSH
3. An editor/IDE of some sort - preferably Intellij/Eclipse
4. Gradle
5. aws cli
6. kubectl
7. Helm
8. CircleCI CLI (optional)

We're using [Gradle](https://gradle.org/) to build the application. This also includes plugins for generating IntelliJ configuration.

### Obtaining environment variables for running locally

To run the app locally, you will need to download the appropriate environment variables from the team
vault in 1Password. These environment variables are stored as a .env file, which docker-compose uses
when starting up the service. If you don't see the team vault, speak to your tech lead to get access.

To begin with, make sure that you have the 1Password CLI installed:

```sh
op version
```

If the command is not found, [follow the steps on the 1Password developer docs to get the CLI set-up](https://developer.1password.com/docs/cli/get-started/).

Once you're ready to run the application:

```sh
./start-local.sh
```

### Decrypting values files

The values YAML files are encrypted using [git-crypt](https://github.com/AGWA/git-crypt).

To be able to view and/or edit these files, you will need to decrypt them first.

You will first need to create a GPG key. See [Create a GPG Key](https://docs.publishing.service.gov.uk/manual/create-a-gpg-key.html) for details on how to do this with `GPGTools` (GUI) or `gpg` (command line).
You can install either from a terminal or just download the UI version.

```
brew update
brew install gpg
brew install git-crypt
```

Once you have done this, a team member who already has access can add your key by running `git-crypt add-gpg-user USER_ID`\* and creating a pull request to this repo.

Once this has been merged you can decrypt your local copy of the repository by running `git-crypt unlock`.

\*`USER_ID` can be your key ID, a full fingerprint, an email address, or anything else that uniquely identifies a public key to GPG (see "HOW TO SPECIFY A USER ID" in the gpg man page).

### DB Configuration

For database changes, we are using [liquibase]() and all the sql scripts stored in the directory (resources/db/changelog/).
This project does not direct access to any of the database (togdata or mla) and all the database calls should be made through the [MAAT-API](https://github.com/ministryofjustice/laa-maat-court-data-api) project.

### Application Set up

Clone Repository

```sh
git clone git@github.com:ministryofjustice/laa-crime-evidence.git

cd crime-evidence
```

Makesure tests all testes are passed by running following ‘gradle’ Command

```sh
./gradlew clean test
```

You will need to build the artifacts for the source code, using `gradle`.

```sh
./gradlew clean build
```

The apps should then startup cleanly if you run

```sh
./startup-local.sh
```

laa-crime-evidence application will be running on http://localhost:8189

### Cloud Platform Set Up

It is advisable to have the cloud platform set up locally.

Follow this link to on board yourself with the LAA cloud platform environment. - https://user-guide.cloud-platform.service.justice.gov.uk/documentation/getting-started/kubectl-config.html#how-to-use-kubectl-to-connect-to-the-cluster

Configure AWS details using aws cli (command - `aws configure`) Set up AWS Access Key ID & AWS Secret Access Key. All other values can be default.

More detail can be found on https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/edit-v2/1756201359.

The terraform scripts for the SQS can be found on https://github.com/ministryofjustice/cloud-platform-environments/tree/master/namespaces/live-1.cloud-platform.service.justice.gov.uk/laa-court-data-adaptor-dev

### Deployment

We have configured a CircleCI code pipelines. You can [log in](https://app.circleci.com/pipelines/github/ministryofjustice/laa-crime-evidence) from here to access the pipeline.

### Open API

We have implemented the Open API standard (with Swagger 3). The web link provides a details Rest API with a schema definition. The link can only from local or from dev environment. The swagger link can be found from [here](http://localhost:8189/open-api/swagger-ui/index.html)

### Debugging Application

Speak to one of the team member and get the docker-compose-debug.yml which will have relevant credentials to run the application on remote Debug Mode.

Run the following command

```sh
 docker-compose -f docker-compose-debug.yml up
```

Make sure Remote Debug Option is set up on your preferred Editor.

### Application Monitoring and Logs

The LAA Crime Means Assessment API has been configured to send the application logs to both AWS Cloud Watch and Sentry.

####Cloud Watch Logs:
To see the Cloud watch logs, you need to have the right user groups and permission. More details about this available here. (link) The application logs are configured with the followings log groups (names).
The application deployed as a Docker container, and the logs can also be found from the AWS ECS logs.

####Sentry
Sentry is a 3rd party application logging and monitoring platform. The platform provides easier searching based on meta-data as well as application monitoring. You can learn more about ['how we have integrated Sentry to improve application logging and monitoring'](https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/2139914261/Integrate+Sentry+to+improve+application+logging+and+monitoring)
There are several alert rules configured on Sentry that will push notification to both email and Slack channel. We have created a dedicated slack channel (named 'laa-crime-apps-logs-alert'). Sentry will push the alert to this channel for a specific type of exceptions. The configuration for Slack alert can be change from a [Sentry dashboard](https://sentry.io/settings/ministryofjustice/projects/laa-crime-evidence/alerts/).

### Mutation PI testing

Mutation testing providing test coverage for Java applications.
Faults (or mutations) are automatically seeded into the code, then your tests are run. If your tests fail then the mutation is killed, if your tests pass then the mutation lived.
Here are some of the key benefits for this type of testing.

- High coverage of testing
- New kinds of test scenarios
- Validate unit test scripts

Once we build the project then run the following command. This will generate a test report under build/reports/pitest/
We want to make sure that the Mutation Coverage for new classes are covered properly

```sh
./gradlew pitest
```

### Further reading

- [Diagrams for LAA and the common platform](https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/1513128006/Diagrams)
- [New Starter Guild](https://dsdmoj.atlassian.net/wiki/spaces/LAA/pages/1391460702/New+Hire+Check+List)
- [Cloud Platform user guide](https://user-guide.cloud-platform.service.justice.gov.uk/#application-logging)
- [Modernisation Platform Team Information](https://user-guide.modernisation-platform.service.justice.gov.uk/#modernisation-platform-team-information)

## JSON Schema to POJO

Gradle plugin that converts json schema files into POJOs (Plain Old Java Objects). See [Extended jsonschema2pojo Gradle plugin](https://github.com/jsonschema2dataclass/js2d-gradle).

The generated POJO files can be found in crime-evidence/build/generated/sources/js2d, after each build, or by running the following command:

```shell
./gradlew clean generateJsonSchema2DataClass
```

### Configuration

In the jsonSchema2Pojo section of crime-means-assessment/build.gradle file, there are a number of settings to that have
been set and are documented inside that section.:

- source.setFrom: The location of the json schema files.
- targetPackage: what package the POJOs should belong to
- includeJsr303Annotations: JSR-303/349 annotations (for schema rules like minimum, maximum, etc)
- dateTimeType: What type to use instead of string
